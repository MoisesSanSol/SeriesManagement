package sm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Dispatcher {

	public static void downloadAllOngoingSeries() throws Exception{
		
		System.out.println("*** Download All Ongoing Series V2***\n");
		
		LocalConf conf = LocalConf.getInstance();
		
		for(String series : conf.ongoingSeries.keySet()) {
			
			String folder = conf.ongoingSeries.get(series);
			Dispatcher.downloadOngoingSeries(series, folder);
			
			System.out.println("");
		}
		
		LocalConf.getInstance().updateEpisodeTrackingFile();
	}
	
	public static void downloadOngoingSeries(String seriesId, String targetFolder) throws Exception{
		
		System.out.println("** Download Ongoing Series");
		System.out.println("* Series Id: " + seriesId);
		System.out.println("* Target Folder: " + targetFolder);
		System.out.println();
		
		LocalConf conf = LocalConf.getInstance();
		HashMap<String,String> failedDownloads = new HashMap<String,String>();
		
		String seriesShort = seriesId.split("/")[1];
		
		String mainSeriesPageUrl = conf.animeFlvSeriesMainPageBaseUrl + seriesId;
		
		Document mainSeriesPage = Jsoup.connect(mainSeriesPageUrl).maxBodySize(0).get();
		ArrayList<String> episodesUrls = WebScrapper.getAllEpisodesUrls(mainSeriesPage);
		
		String seriesStatus = WebScrapper.getSeriesStatus(mainSeriesPage);
		if(seriesStatus.equals("Finalizado")){
			Audit.getInstance().addLog(targetFolder + " status: " + seriesStatus);
		}
		
		File seriesFolder = new File(conf.ongoingSeriesFolder.getAbsolutePath() + "/" + targetFolder + "/");
		conf.checkFolderExistence(seriesFolder);
		
		ArrayList<String> episodes = conf.episodeTracking.get(seriesShort);
		
		for(String episodeUrl : episodesUrls){
			System.out.println(episodeUrl);
			
			String episodeNumberRaw = episodeUrl.replaceAll(".+-", "") ;
			String episodeNumber = String.format("%02d", Integer.parseInt(episodeNumberRaw));
			
			if(!episodes.contains(episodeNumber) && Integer.parseInt(episodeNumber) <= conf.episodeCap){
			
				File targetFile = new File(conf.ongoingSeriesFolder.getAbsolutePath() + "/" + targetFolder + "/" + seriesShort + "_" + episodeNumber + ".mp4");
			
				Document episodePage = Jsoup.connect(episodeUrl).maxBodySize(0).get();
				String zippyUrl = WebScrapper.getZippyshareUrl(episodePage);
				String fileUrl = WebScrapper.getFileUrlFromZippyshareV3(zippyUrl);
				if(!fileUrl.equals("NotFound")){
					File localTargetFile = new File(conf.downloadTargetFolder.getAbsolutePath() + "/" + seriesShort + "_" + episodeNumber + ".mp4");
					DownloadHelper.downloadVideo(fileUrl, localTargetFile);
					if(localTargetFile.length() < 100000) {
						Audit.getInstance().addLog("New " + targetFolder + " Episode: " + episodeNumber + " has size: " + localTargetFile.length() + ", it is probably broken. Not copying it.");
					}
					else {
						FileUtils.copyFile(localTargetFile, targetFile);
						episodes.add(episodeNumber);
						Audit.getInstance().addLog("New " + targetFolder + " Episode: " + episodeNumber);
					}
				}
				else{
					String openloadUrl = WebScrapper.getOpenloadUrl(episodePage);
					System.out.println("Alternative download: " + openloadUrl);
					failedDownloads.put(episodeNumberRaw, openloadUrl);
					Audit.getInstance().addLog("No Zippyshare for new " + targetFolder + " Episode: " + episodeNumber);
				}
			}
			else{
				System.out.println("*** Episode Exists ***: " + episodeNumber);
			}
		}
		
		if(!failedDownloads.isEmpty()){
			String seriesFileId = WebScrapper.getSeriesId(mainSeriesPage);
			String targetFolderPath = conf.ongoingSeriesFolder.getAbsolutePath() + "/" + targetFolder + "/";
			DownloadHelper.downloadHelpForOpenload(failedDownloads, seriesShort, seriesFileId, targetFolderPath);
		}
	}
}

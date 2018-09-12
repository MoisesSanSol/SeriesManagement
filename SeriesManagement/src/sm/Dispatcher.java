package sm;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
		//System.out.println(mainSeriesPage.html());
		
		ArrayList<String> episodesUrls = WebScrapper.getAllEpisodesUrls(mainSeriesPage);
		
		String seriesStatus = WebScrapper.getSeriesStatus(mainSeriesPage);
		if(seriesStatus.equals("Finalizado")){
			Audit.getInstance().addLog(targetFolder + " status: " + seriesStatus);
		}
		
		File seriesFolder = new File(conf.ongoingSeriesFolder.getAbsolutePath() + "/" + targetFolder + "/");
		conf.checkFolderExistence(seriesFolder);
		
		ArrayList<String> episodes = conf.episodeTracking.get(seriesId);
		
		for(String episodeUrl : episodesUrls){
			System.out.println(episodeUrl);
			
			String episodeNumberRaw = episodeUrl.replaceAll(".+-", "") ;
			
			String episodeNumber = "999";
			
			try {
				episodeNumber = String.format("%02d", Integer.parseInt(episodeNumberRaw));
			}
			catch(NumberFormatException ex) {
				ex.printStackTrace();
				Audit.getInstance().addLog("Episode Number Error: " + episodeNumberRaw + ", Forced Download: " + episodeUrl);
				episodeNumber = episodeNumberRaw;
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
					Dispatcher.handleNoZippyshareEpisode(episodeNumberRaw, episodePage, failedDownloads);
					Audit.getInstance().addLog("No Zippyshare for new " + targetFolder + " Episode: " + episodeNumber);
				}
			}
			
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
					Dispatcher.handleNoZippyshareEpisode(episodeNumberRaw, episodePage, failedDownloads);
					Audit.getInstance().addLog("No Zippyshare for new " + targetFolder + " Episode: " + episodeNumber);
				}
			}
			else{
				System.out.println("*** Episode Exists ***: " + episodeNumber);
			}
		}
		
		if(!failedDownloads.isEmpty()){

			String targetFolderPath = conf.ongoingSeriesFolder.getAbsolutePath() + "/" + targetFolder + "/";
			DownloadHelper.downloadHelpForAlternatives(failedDownloads, seriesShort, targetFolderPath);
		}
	}
	
	public static void handleNoZippyshareEpisode(String episodeNumberRaw, Document episodePage, HashMap<String,String> failedDownloads) throws Exception{
		String episodeFileId = WebScrapper.getSeriesId(episodePage);
		String openloadUrl = WebScrapper.getOpenloadUrl(episodePage);
		String megaUrl = WebScrapper.getMegaUrl(episodePage);
		System.out.println("Alternative Openload download: " + openloadUrl);
		System.out.println("Alternative Mega download: " + megaUrl);
		failedDownloads.put(episodeFileId + "#" + episodeNumberRaw + "#openload", openloadUrl);
		failedDownloads.put(episodeFileId + "#" + episodeNumberRaw + "#mega", megaUrl);
	}
	
	public static void updateSeriesInfo() throws Exception{
		
		LocalConf conf = LocalConf.getInstance();
		
		for(Series series : conf.series) {
			String mainSeriesPageUrl = conf.animeFlvSeriesMainPageBaseUrl + series.seriesPage;
			
			Document mainSeriesPage = Jsoup.connect(mainSeriesPageUrl).maxBodySize(0).get();
			//System.out.println(mainSeriesPage.html());
			
			
			//series.seriesFileId = WebScrapper.getSeriesId(mainSeriesPage);
			String seriesStatus = WebScrapper.getSeriesStatus(mainSeriesPage);
			if(seriesStatus.equals("Finalizado")) {
				series.finished = true;
			}
			
			ArrayList<String> episodesUrls = WebScrapper.getAllEpisodesUrls(mainSeriesPage);
			
			for(String episodeUrl : episodesUrls) {
				String episodeNumberRaw = episodeUrl.replaceAll(".+-", "") ;
				String episodeNumber = "999";
				
				try {
					episodeNumber = String.format("%02d", Integer.parseInt(episodeNumberRaw));
				}
				catch(NumberFormatException ex) {
					//TODO
				}
				series.episodesAvailable.put(episodeNumber, mainSeriesPageUrl);
			}
		}
	}
	
	public static void cleanFinishedSeries() throws Exception{
		
		System.out.println("*** Clean Finished Series ***\n");
		
		Dispatcher.updateSeriesInfo();
		LocalConf conf = LocalConf.getInstance();
		ArrayList<Series> ongoingSeriesList = conf.series;
		ArrayList<Series> finishedSeriesList = ConfHandler.getFinishedSeriesList();
		
		for(Series ongoingSeries : ongoingSeriesList){
			
			System.out.println("** Checking series: " + ongoingSeries.seriesName);
			
			if(ongoingSeries.finished){
				
				System.out.println("* Finished");
				
				if(ongoingSeries.episodesAvailable.size() == ongoingSeries.episodesDownloaded.size()){
				
					System.out.println("* Everything downloaded!");
					
					File ongoingSeriesFolder = new File(conf.ongoingSeriesFolderPath + ongoingSeries.seriesName);
					File finishedSeriesFolder = new File(conf.finishedSeriesFolderPath + ongoingSeries.seriesName);
					
					Files.move(ongoingSeriesFolder.toPath(), finishedSeriesFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
					
					finishedSeriesList.add(ongoingSeries);
					ongoingSeriesList.remove(ongoingSeries);
				}
			}
			
		}
		
		ConfHandler.setFinishedSeriesList(finishedSeriesList);
		ConfHandler.setOngoingSeriesList(ongoingSeriesList);
	}
}

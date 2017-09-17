package sm;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Dispatcher {

	public static void downloadAllEpisodes(String seriesId, String targetFolder) throws Exception{
		
		System.out.println("*** Download All Episodes ***");
		System.out.println("* Series Id: " + seriesId);
		System.out.println("* Target Folder: " + targetFolder);
		System.out.println();
		
		LocalConf conf = new LocalConf();
		
		String seriesShort = seriesId.split("/")[1];
		
		String mainSeriesPageUrl = LocalConf.animeFlvSeriesMainPageBaseUrl + seriesId;
		
		Document mainSeriesPage = Jsoup.connect(mainSeriesPageUrl).maxBodySize(0).get();
		ArrayList<String> episodesUrls = WebScrapper.getAllEpisodesUrls(mainSeriesPage);
		
		HashMap<String,String> failedDownloads = new HashMap<String,String>();
		
		for(String episodeUrl : episodesUrls){
			System.out.println(episodeUrl);
			String episodeNumberRaw = episodeUrl.replaceAll(".+-", "");
			String episodeNumber = String.format("%02d", Integer.parseInt(episodeNumberRaw));
			File targetFile = new File(conf.downloadTargetFolder.getAbsolutePath() + "/" + targetFolder + "/" + seriesShort + "_" + episodeNumber + ".mp4");
			
			if(!targetFile.exists()){
				Document episodePage = Jsoup.connect(episodeUrl).maxBodySize(0).get();
				String zippyUrl = WebScrapper.getZippyshareUrl(episodePage);
				String fileUrl = WebScrapper.getFileUrlFromZippyshare(zippyUrl);
				if(!fileUrl.equals("NotFound")){
					DownloadHelper.downloadVideo(fileUrl, targetFile);
				}
				else{
					String openloadUrl = WebScrapper.getOpenloadUrl(episodePage);
					System.out.println("Alternative download: " + openloadUrl);
					failedDownloads.put(openloadUrl, episodeNumber);
				}
			}
			else{
				
				System.out.println("\tEpisode " + episodeNumber + " already exists");
			}
		}
		
		if(!failedDownloads.isEmpty()){
			String seriesFileId = WebScrapper.getSeriesId(mainSeriesPage);
			String targetFolderPath = conf.downloadTargetFolder.getAbsolutePath() + "/" + targetFolder + "/";
			DownloadHelper.downloadHelpForOpenload(failedDownloads, seriesShort, seriesFileId, targetFolderPath);
		}
		
	}

	
	public static void downloadOngoingSeries(String seriesId, String targetFolder) throws Exception{
		
		System.out.println("*** Download Ongoing Series ***");
		System.out.println("* Series Id: " + seriesId);
		System.out.println("* Target Folder: " + targetFolder);
		System.out.println();
		
		LocalConf conf = new LocalConf();
		
		String seriesShort = seriesId.split("/")[1];
		
		String mainSeriesPageUrl = LocalConf.animeFlvSeriesMainPageBaseUrl + seriesId;
		
		Document mainSeriesPage = Jsoup.connect(mainSeriesPageUrl).maxBodySize(0).get();
		ArrayList<String> episodesUrls = WebScrapper.getAllEpisodesUrls(mainSeriesPage);
		
		for(String episodeUrl : episodesUrls){
			System.out.println(episodeUrl);
			String episodeNumberRaw = episodeUrl.replaceAll(".+-", "") ;
			String episodeNumber = String.format("%02d", Integer.parseInt(episodeNumberRaw));
			File targetFile = new File(conf.ongoingSeriesFolder.getAbsolutePath() + "/" + targetFolder + "/" + seriesShort + "_" + episodeNumber + ".mp4");
			
			if(!targetFile.exists()){
				Document episodePage = Jsoup.connect(episodeUrl).maxBodySize(0).get();
				String zippyUrl = WebScrapper.getZippyshareUrl(episodePage);
				String fileUrl = WebScrapper.getFileUrlFromZippyshare(zippyUrl);
				File localTargetFile = new File(conf.downloadTargetFolder.getAbsolutePath() + "/Movies/" + seriesShort + "_" + episodeNumber + ".mp4");
				DownloadHelper.downloadVideo(fileUrl, localTargetFile);
				FileUtils.copyFile(localTargetFile, targetFile);
			}
			else{
				System.out.println("*** Episode Exists ***: " + episodeNumber);
			}
		}
		
	}
	
	public static void downloadAllOngoingSeries() throws Exception{
		
		System.out.println("*** Download All Ongoing Series ***\n");
		
		Properties prop = new Properties();
		InputStream input = new FileInputStream("Conf/OngoingSeries.txt");

		prop.load(input);

		Enumeration<?> allSeries = prop.propertyNames();
		
		while (allSeries.hasMoreElements()) {
			
			String key = (String) allSeries.nextElement();
			String value = prop.getProperty(key);
			Dispatcher.downloadOngoingSeries(key, value);
			
			System.out.println("");
		}
	}

}

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
		
		LocalConf conf = LocalConf.getInstance();
		
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
				String fileUrl = WebScrapper.getFileUrlFromZippyshareV2(zippyUrl);
				if(!fileUrl.equals("NotFound")){
					DownloadHelper.downloadVideo(fileUrl, targetFile);
				}
				else{
					String openloadUrl = WebScrapper.getOpenloadUrl(episodePage);
					System.out.println("Alternative download: " + openloadUrl);
					failedDownloads.put(openloadUrl, episodeNumberRaw);
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
		
		LocalConf conf = LocalConf.getInstance();
		
		conf.checkFolderExistence(conf.downloadTargetFolder);
		
		String seriesShort = seriesId.split("/")[1];
		
		String mainSeriesPageUrl = LocalConf.animeFlvSeriesMainPageBaseUrl + seriesId;
		
		Document mainSeriesPage = Jsoup.connect(mainSeriesPageUrl).maxBodySize(0).get();
		ArrayList<String> episodesUrls = WebScrapper.getAllEpisodesUrls(mainSeriesPage);
		
		String seriesStatus = WebScrapper.getSeriesStatus(mainSeriesPage);
		if(seriesStatus.equals("Finalizado")){
			Audit.getInstance().addLog(targetFolder + " status: " + seriesStatus);
		}
		File seriesFolder = new File(conf.ongoingSeriesFolder.getAbsolutePath() + "/" + targetFolder + "/");
		if(!seriesFolder.exists()) {
			seriesFolder.mkdir();
			Audit.getInstance().addLog(targetFolder + " did not exist. New folder created.");
		}
		
		HashMap<String,String> failedDownloads = new HashMap<String,String>();
		
		for(String episodeUrl : episodesUrls){
			System.out.println(episodeUrl);
			String episodeNumberRaw = episodeUrl.replaceAll(".+-", "") ;
			String episodeNumber = String.format("%02d", Integer.parseInt(episodeNumberRaw));
			File targetFile = new File(conf.ongoingSeriesFolder.getAbsolutePath() + "/" + targetFolder + "/" + seriesShort + "_" + episodeNumber + ".mp4");
			
			if(!targetFile.exists()){
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
						Audit.getInstance().addLog("New " + targetFolder + " Episode: " + episodeNumber);
					}
				}
				else{
					String openloadUrl = WebScrapper.getOpenloadUrl(episodePage);
					System.out.println("Alternative download: " + openloadUrl);
					failedDownloads.put(openloadUrl, episodeNumberRaw);
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

	public static void checkAllOngoingSeriesStatus() throws Exception{
		
		System.out.println("*** Check All Ongoing Series Status***\n");
		
		Properties prop = new Properties();
		InputStream input = new FileInputStream("Conf/OngoingSeries.txt");

		prop.load(input);

		Enumeration<?> allSeries = prop.propertyNames();
		
		while (allSeries.hasMoreElements()) {
			
			String key = (String) allSeries.nextElement();
			String value = prop.getProperty(key);
			String mainSeriesPageUrl = LocalConf.animeFlvSeriesMainPageBaseUrl + key;
			Document mainSeriesPage = Jsoup.connect(mainSeriesPageUrl).maxBodySize(0).get();
			String status = WebScrapper.getSeriesStatus(mainSeriesPage);
			System.out.println(value + ": " + status);
		}
		
	}
	
}

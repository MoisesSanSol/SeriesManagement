package sm;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Dispatcher {

	public static void downloadAllEpisodes(String seriesId, String targetFolder) throws Exception{
		
		
		LocalConf conf = new LocalConf();
		
		String seriesShort = seriesId.split("/")[1];
		
		String mainSeriesPageUrl = LocalConf.animeFlvSeriesMainPageBaseUrl + seriesId;
		
		Document mainSeriesPage = Jsoup.connect(mainSeriesPageUrl).maxBodySize(0).get();
		ArrayList<String> episodesUrls = WebScrapper.getAllEpisodesUrls(mainSeriesPage);
		
		for(String episodeUrl : episodesUrls){
			System.out.println(episodeUrl);
			String episodeNumber = episodeUrl.replaceAll(".+-", "");
			File targetFile = new File(conf.downloadTargetFolder.getAbsolutePath() + "/" + targetFolder + "/" + seriesShort + "_" + episodeNumber + ".mp4");
			
			if(!targetFile.exists()){
				Document episodePage = Jsoup.connect(episodeUrl).maxBodySize(0).get();
				String zippyUrl = WebScrapper.getZippyshareUrl(episodePage);
				String fileUrl = WebScrapper.getFileUrlFromZippyshare(zippyUrl);
				DownloadHelper.downloadVideo(fileUrl, targetFile);
			}
			else{
				System.out.println("*** Episode Exists ***: " + episodeNumber);
			}
		}
		
	}

	
	public static void downloadOngoingSeries(String seriesId, String targetFolder) throws Exception{
		
		
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
		
		Properties prop = new Properties();
		InputStream input = new FileInputStream("Conf/OngoingSeries.txt");

		prop.load(input);

		Enumeration<?> e = prop.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = prop.getProperty(key);
			Dispatcher.downloadOngoingSeries(key, value);
		}
	}

}

package sm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

// Singleton Configuration class.
public class LocalConf {

	// URLs
	public String animeFlvBaseUrl = "https://animeflv.net";
	public String animeFlvSeriesMainPageBaseUrl = "https://animeflv.net/anime/";
	
	// Configuration file
	public String localConfFileName = "LocalConfiguration.properties";
	
	// Episode tracking file
	public String ongoingSeriesFileName = "OngoingSeries.txt";
	public String episodeTrackingFileName = "OngoingProgress.txt";

	// Folders
	public String ongoingSeriesFolderPath;
	public String downloadTargetFolderPath;
	public File ongoingSeriesFolder;
	public File downloadTargetFolder;
	
	// Items and others
	public HashMap<String,String> ongoingSeries; 
	public HashMap<String,ArrayList<String>> episodeTracking; 
	public int episodeCap = 99;
	
	// Singleton instance
	private static LocalConf instance;
	

	private LocalConf() throws Exception{
		this.loadLocalConfiguration_Ongoing();
	}
	
	public static LocalConf getInstance() throws Exception{
      if(instance == null) {
          instance = new LocalConf();
       }
       return instance;
	}
	
	public void loadLocalConfiguration_Ongoing() throws Exception{

		InputStream localConfInput = null;
		
		try {

			// Folders
			Properties localConf = new Properties();
			
			String localConfFilePath = "Conf/" + this.localConfFileName;
			localConfInput = new FileInputStream(localConfFilePath);

			localConf.load(localConfInput);
			
			this.ongoingSeriesFolderPath = localConf.getProperty("ongoingSeriesFolder");
			this.ongoingSeriesFolder = new File(ongoingSeriesFolderPath);
			this.downloadTargetFolderPath = localConf.getProperty("downloadTargetFolder");
			this.downloadTargetFolder = new File(downloadTargetFolderPath);
			
			this.checkFolderExistence(this.downloadTargetFolder);

			// Ongoing Series
			this.loadOngoingSeries();
			
			// Episode Tracking
			this.loadEpisodeTracking();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (localConfInput != null) {
				try {
					localConfInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void loadLocalConfiguration() throws Exception{

		InputStream localConfInput = null;
		
		try {

			// Folders
			Properties localConf = new Properties();
			
			String localConfFilePath = "Conf/" + this.localConfFileName;
			localConfInput = new FileInputStream(localConfFilePath);

			localConf.load(localConfInput);
			
			String ongoingSeriesFolderPath = localConf.getProperty("ongoingSeriesFolder");
			this.ongoingSeriesFolder = new File(ongoingSeriesFolderPath);
			String downloadTargetFolderPath = localConf.getProperty("downloadTargetFolder");
			this.downloadTargetFolder = new File(downloadTargetFolderPath);
			this.checkFolderExistence(this.downloadTargetFolder);

			// Ongoing Series
			this.loadOngoingSeries();
			
			// Episode Tracking
			this.loadEpisodeTracking();
			
			// Others
			String episodeCapStr = localConf.getProperty("episodeCap");
			this.episodeCap = Integer.parseInt(episodeCapStr);
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (localConfInput != null) {
				try {
					localConfInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void loadEpisodeTracking() throws Exception{
		
		this.episodeTracking = new HashMap<String,ArrayList<String>>();
		
		for(String series : this.ongoingSeries.keySet()) {
			
			ArrayList<String> episodeNumbers = new ArrayList<String>();
			
			String seriesFolderPath = this.ongoingSeriesFolderPath + this.ongoingSeries.get(series);
			File seriesFolder = new File(seriesFolderPath);
			this.checkFolderExistence(seriesFolder);
			for (File episodeFile : seriesFolder.listFiles()) {
				String episodeNumberStr = episodeFile.getName().replaceAll(".+_(\\d+).mp4", "$1");
				episodeNumbers.add(episodeNumberStr);
			}
			
			this.episodeTracking.put(series, episodeNumbers);
		}
		
	}
	
	private void loadEpisodeTracking_File(){
		
		InputStream episodeTrackingInput = null;
		
		try{
		
			Properties episodeTrackingProps = new Properties();
			
			String episodeTrackingFilePath = this.ongoingSeriesFolder.getPath() + "\\" + this.episodeTrackingFileName;
			episodeTrackingInput = new FileInputStream(episodeTrackingFilePath);
	
			episodeTrackingProps.load(episodeTrackingInput);
	
			this.episodeTracking = new HashMap<String,ArrayList<String>>();
			
			for(String series : this.ongoingSeries.keySet()) {
				ArrayList<String> episodes = new ArrayList<String>();
				if(episodeTrackingProps.get(series) != null){
					String serializedEpisodes = episodeTrackingProps.getProperty(series);
					String[] episodesArray = serializedEpisodes.split(",");
					episodes.addAll(Arrays.asList(episodesArray));
				}
				this.episodeTracking.put(series, episodes);
			}
			
		}
		catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (episodeTrackingInput != null) {
				try {
					episodeTrackingInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void loadOngoingSeries(){
		
		InputStream ongoingSeriesInput = null;
		
		try{
		
			Properties ongoingSeriesProps = new Properties();
			
			String ongoingSeriesFilePath = this.ongoingSeriesFolderPath + this.ongoingSeriesFileName;
			ongoingSeriesInput = new FileInputStream(ongoingSeriesFilePath);

			ongoingSeriesProps.load(ongoingSeriesInput);

			this.ongoingSeries = new HashMap<String,String>();
			
			for(Object seriesObj : ongoingSeriesProps.keySet()){
				String series = (String)seriesObj;
				String folder = ongoingSeriesProps.getProperty(series);
				this.ongoingSeries.put(series, folder);
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (ongoingSeriesInput != null) {
				try {
					ongoingSeriesInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void checkFolderExistence(File folder) throws Exception{
		if(!folder.exists()){
			folder.mkdir();
			Audit.getInstance().addLog(folder + " did not exist. New folder created.");
		}
	}
	
	public void updateEpisodeTrackingFile() throws Exception{

		Properties prop = new Properties();
		
		for(Object seriesObj : this.episodeTracking.keySet()){
			String series = (String)seriesObj;
			ArrayList<String> episodes = this.episodeTracking.get(series);
			String serializedEpisodes = episodes.toString().replace("[", "").replace("]", "").replace(" ", "");
			prop.setProperty(series, serializedEpisodes);
		}
		
		String episodeTrackingFilePath = this.ongoingSeriesFolder.getAbsolutePath() + "\\" + this.episodeTrackingFileName;
		FileOutputStream output = new FileOutputStream(episodeTrackingFilePath);
		prop.store(output, null);
		output.close();
	}
	
	public void updateEpisodeTrackingFile_ManuallyAddedFiles() throws Exception{
		
		System.out.println("** Update Episode Tracking File : Manually Added Files");
		
		LocalConf conf = LocalConf.getInstance();
		
		HashMap<String,ArrayList<String>> episodeTracking = new HashMap<String,ArrayList<String>>();
		
		for(String series : conf.ongoingSeries.keySet()) {
		
			String value = conf.ongoingSeries.get(series);

			String seriesShort = series.split("/")[1];
			File seriesFolder = new File(conf.ongoingSeriesFolder.getAbsolutePath() + "/" + value + "/");
			
			System.out.println(seriesShort);
			
			ArrayList<String> episodes = conf.episodeTracking.get(seriesShort);
			
			if(episodes == null){
				System.out.println("* Found series without tracking: " + seriesShort);
				Audit.getInstance().addLog("* Found series without tracking: " + seriesShort);
				episodes = new ArrayList<String>();
				episodeTracking.put(seriesShort, episodes);
			}
			
			for(File episodeFile : seriesFolder.listFiles()){
				
				String episodeNumber = episodeFile.getName().replaceAll(".+_(\\d+).mp4", "$1");
				System.out.println("* Found episode: " + episodeNumber);
				
				if(!episodes.contains(episodeNumber)){
					System.out.println("* Missing Episode: " + episodeNumber);
					Audit.getInstance().addLog("* Missing Episode: " + episodeNumber);
					episodes.add(episodeNumber);
				}
			}
			
			episodeTracking.put(seriesShort, episodes);
			
			System.out.println("");
		}
		
		conf.episodeTracking = episodeTracking;
		conf.updateEpisodeTrackingFile();
		
	}
	
	public void updateEpisodeTrackingFile_FromScratch() throws Exception{
		
		/*System.out.println("** Update Episode Tracking File : From Scratch");
		
		HashMap<String,ArrayList<String>> episodeTracking = new HashMap<String,ArrayList<String>>();
		
		for(String series : this.ongoingSeries.keySet()) {

			System.out.println("* Series: " + series);
			
			int latestEpisode = -1;
			
			String folder = conf.ongoingSeries.get(series);

			File seriesFolder = new File(conf.ongoingSeriesFolder.getAbsolutePath() + "/" + folder + "/");
			
			for(File episodeFile : seriesFolder.listFiles()){
				
				String episodeNumberStr = episodeFile.getName().replaceAll(".+_(\\d+).mp4", "$1");
				int episodeNumber = Integer.parseInt(episodeNumberStr);

				if(episodeNumber > latestEpisode){
					latestEpisode = episodeNumber;
				}
				
				System.out.println("* Episode: " + episodeNumber);
			}
			
			System.out.println("* Latest Episode: " + latestEpisode);
			
			ArrayList<String> episodes = new ArrayList<String>();
			
			for (int i = 1; i <= latestEpisode; i++){
				String episodeNumber = String.format("%02d", i);
				episodes.add(episodeNumber);
			}
			
			String seriesShort = series.split("/")[1];
			episodeTracking.put(seriesShort, episodes);
			
			System.out.println("");
		}
		
		conf.episodeTracking = episodeTracking;
		conf.updateEpisodeTrackingFile();*/
	}
	
	public static void createOngoingFolders() throws Exception{
		
		System.out.println("** Update Episode Tracking File : From Scratch");
		
		LocalConf conf = LocalConf.getInstance();
		
		for(String series : conf.ongoingSeries.keySet()) {

			System.out.println("* Series: " + series);
			
			String folder = conf.ongoingSeries.get(series);

			String seriesFolderPath = conf.ongoingSeriesFolder.getAbsolutePath() + "/" + folder + "/";
			File seriesFolder = new File(seriesFolderPath);
			
			conf.checkFolderExistence(seriesFolder);
			
			for(File episodeFile : conf.ongoingSeriesFolder.listFiles()){

				if(episodeFile.getName().contains(series)) {
					File newEpisodeFile = new File(seriesFolderPath + episodeFile.getName());
					
					episodeFile.renameTo(newEpisodeFile);
				}

			}
			
			

		}
		
		
	}
	
}

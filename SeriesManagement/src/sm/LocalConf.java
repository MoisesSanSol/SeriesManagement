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
	public String folderConfFile = "LocalConfiguration.properties";
	
	// Episode tracking file
	public String episodeTrackingFile = "OngoingProgress.properties";

	// Folders
	public File ongoingSeriesFolder;
	public File downloadTargetFolder;
	
	// Items
	public HashMap<String,ArrayList<String>> episodeTracking; 
	
	// Singleton instance
	private static LocalConf instance;
	

	private LocalConf() throws Exception{
		this.loadLocalConfiguration();
	}
	
	public static LocalConf getInstance() throws Exception{
      if(instance == null) {
          instance = new LocalConf();
       }
       return instance;
	}
	
	public void loadLocalConfiguration() throws Exception{

		InputStream folderConfInput = null;
		InputStream episodeTrackingInput = null;
		
		try {

			// Folders
			Properties folderConf = new Properties();
			
			String folderConfFilePath = "Conf/" + this.folderConfFile;
			folderConfInput = new FileInputStream(folderConfFilePath);

			folderConf.load(folderConfInput);
			
			String ongoingSeriesFolderPath = folderConf.getProperty("ongoingSeriesFolder");
			this.ongoingSeriesFolder = new File(ongoingSeriesFolderPath);
			String downloadTargetFolderPath = folderConf.getProperty("downloadTargetFolder");
			this.downloadTargetFolder = new File(downloadTargetFolderPath);
			this.checkFolderExistence(this.downloadTargetFolder);
			
			// Episodes
			Properties episodeTrackingProps = new Properties();
			
			String episodeTrackingFilePath = ongoingSeriesFolderPath + this.episodeTrackingFile;
			episodeTrackingInput = new FileInputStream(episodeTrackingFilePath);

			episodeTrackingProps.load(episodeTrackingInput);

			this.episodeTracking = new HashMap<String,ArrayList<String>>();
			
			for(Object seriesObj : episodeTrackingProps.keySet()){
				String series = (String)seriesObj;
				String serializedEpisodes = episodeTrackingProps.getProperty(series);
				ArrayList<String> episodes = new ArrayList<String>();
				if(!serializedEpisodes.equals("None Yet")){
					String[] episodesArray = serializedEpisodes.split(",");
					episodes.addAll(Arrays.asList(episodesArray));
				}
				this.episodeTracking.put(series, episodes);
			}
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (folderConfInput != null) {
				try {
					folderConfInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (episodeTrackingInput != null) {
				try {
					episodeTrackingInput.close();
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
		
		String episodeTrackingFilePath = this.ongoingSeriesFolder.getAbsolutePath() + "\\" + this.episodeTrackingFile;
		FileOutputStream output = new FileOutputStream(episodeTrackingFilePath);
		prop.store(output, null);
		output.close();
		
	}
	
	public void updateEpisodeTrackingFile_ManuallyAddedFiles() throws Exception{
		
		System.out.println("*** Update Episode Tracking File : Manually Added Files");
		
		LocalConf conf = LocalConf.getInstance();
		
		HashMap<String,ArrayList<String>> episodeTracking = new HashMap<String,ArrayList<String>>();
		
		Properties prop = new Properties();
		InputStream input = new FileInputStream("Conf/OngoingSeries.txt");

		prop.load(input);

		input.close();
		
		Enumeration<?> allSeries = prop.propertyNames();
		
		while (allSeries.hasMoreElements()) {
		
			String key = (String) allSeries.nextElement();
			String value = prop.getProperty(key);

			String seriesShort = key.split("/")[1];
			File seriesFolder = new File(conf.ongoingSeriesFolder.getAbsolutePath() + "/" + value + "/");
			
			System.out.println(seriesShort);
			
			ArrayList<String> episodes = conf.episodeTracking.get(seriesShort);
			
			for(File episodeFile : seriesFolder.listFiles()){
				
				String episodeNumber = episodeFile.getName().replaceAll(".+?_(\\d\\d).mp4", "$1");
				
				if(!episodes.contains(episodeNumber)){
					episodes.add(episodeNumber);
				}
				
				System.out.println(episodeNumber);
			}
			
			episodeTracking.put(seriesShort, episodes);
			
			System.out.println("");
		}
		
		conf.episodeTracking = episodeTracking;
		conf.updateEpisodeTrackingFile();
		
	}
}

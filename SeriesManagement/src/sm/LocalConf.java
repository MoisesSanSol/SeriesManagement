package sm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

// Singleton Configuration class.
public class LocalConf {

	// URLs
	public String animeFlvBaseUrl = "https://animeflv.net";
	public String animeFlvSeriesMainPageBaseUrl = "https://animeflv.net/anime/";
	
	// Configuration file
	public String localConfFileName = "LocalConfiguration.properties";
	
	// Episode tracking files
	public String ongoingSeriesFileName = "OngoingSeries.txt";
	public String finishedSeriesFileName = "FinishedSeries.txt";

	// Folders
	public String ongoingSeriesFolderPath;
	public File ongoingSeriesFolder;
	public String downloadTargetFolderPath;
	public File downloadTargetFolder;
	public String configurationFilesFolderPath;
	public File configurationFilesFolder;
	public String finishedSeriesFolderPath;
	public File finishedSeriesFolder;
	public String pendingSeriesFolderPath;
	public File pendingSeriesFolder;
	public String pastSeriesFolderPath;
	public File pastSeriesFolder;
	
	// Items and others
	
	public ArrayList<Series> series;
	
	public HashMap<String,String> ongoingSeries; 
	public HashMap<String,ArrayList<String>> episodeTracking; 
	public int episodeCap = 99;
	
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

		InputStream localConfInput = null;
		
		try {

			// Folders
			Properties localConf = new Properties();
			
			String localConfFilePath = "Conf/" + this.localConfFileName;
			localConfInput = new FileInputStream(localConfFilePath);

			localConf.load(localConfInput);

			this.configurationFilesFolderPath = localConf.getProperty("configurationFilesFolder");
			this.configurationFilesFolder = new File(this.configurationFilesFolderPath);
			
			this.downloadTargetFolderPath = localConf.getProperty("downloadTargetFolder");
			this.downloadTargetFolder = new File(this.downloadTargetFolderPath);
			
			this.ongoingSeriesFolderPath = localConf.getProperty("ongoingSeriesFolder");
			this.ongoingSeriesFolder = new File(this.ongoingSeriesFolderPath);
			this.finishedSeriesFolderPath = localConf.getProperty("finishedSeriesFolder");
			this.finishedSeriesFolder = new File(this.finishedSeriesFolderPath);
			this.pendingSeriesFolderPath = localConf.getProperty("pendingSeriesFolder");
			this.pendingSeriesFolder = new File(this.pendingSeriesFolderPath);
			this.pastSeriesFolderPath = localConf.getProperty("pastSeriesFolder");
			this.pastSeriesFolder = new File(this.pastSeriesFolderPath);
			
			this.checkFolderExistence(this.downloadTargetFolder);

			// Ongoing Series
			this.loadOngoingSeries();
			
			// Episode Tracking
			this.loadEpisodeTracking();
			
			// New Ongoing Series structure
			this.loadSeries();
			
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
	
	private void loadOngoingSeries(){
		
		InputStream ongoingSeriesInput = null;
		
		try{
		
			Properties ongoingSeriesProps = new Properties();
			
			String ongoingSeriesFilePath = this.configurationFilesFolderPath + this.ongoingSeriesFileName;
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
	
	private void loadSeries(){
		
		this.series = new ArrayList<Series>();
		
		for(String seriesPage : this.ongoingSeries.keySet()){
			
			Series series = new Series();

			series.seriesPage = seriesPage;
			series.seriesName = this.ongoingSeries.get(seriesPage);
			series.seriesFileId = seriesPage.split("/")[0];
			series.seriesFileName = seriesPage.split("/")[1];

			series.episodesDownloaded = this.episodeTracking.get(seriesPage);	
			
			this.series.add(series);
		}
		
	}
}

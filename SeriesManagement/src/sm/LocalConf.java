package sm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// Singleton Configuration class.
public class LocalConf {

	public File ongoingSeriesFolder;
	public File downloadTargetFolder;
	public static String animeFlvBaseUrl = "https://animeflv.net";
	public static String animeFlvSeriesMainPageBaseUrl = "https://animeflv.net/anime/";
	
	private static LocalConf instance;
	
	private LocalConf(){
		this.loadLocalConfiguration();
	}
	
	public static LocalConf getInstance(){
      if(instance == null) {
          instance = new LocalConf();
       }
       return instance;
	}
	
	public void loadLocalConfiguration(){
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("Conf/LocalConfiguration.properties");

			prop.load(input);
			
			// Folders
			String ongoingSeriesFolderPath = prop.getProperty("ongoingSeriesFolder");
			this.ongoingSeriesFolder = new File(ongoingSeriesFolderPath);
			String downloadTargetFolderPath = prop.getProperty("downloadTargetFolder");
			this.downloadTargetFolder = new File(downloadTargetFolderPath);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void checkFolderExistence(File folder){
		if(!folder.exists()){
			folder.mkdir();
			Audit.getInstance().addLog(folder + " did not exist. New folder created.");
		}
	}
	
}

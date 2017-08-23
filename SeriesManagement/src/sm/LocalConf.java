package sm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LocalConf {

	public File ongoingSeriesFolder;
	public File downloadTargetFolder;
	
	public void loadLocalConfiguration(){
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("LocalConfiguration.properties");

			prop.load(input);

			String ongoingSeriesFolderPath = prop.getProperty("ongoingSeriesFolder");
			String downloadTargetFolderPath = prop.getProperty("downloadTargetFolder");

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
	
}

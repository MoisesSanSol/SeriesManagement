package sm;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class DownloadHelper {

	public static void downloadVideo(String url, File target) throws Exception{
		
		try {
		    Thread.sleep(1000);
		} catch ( java.lang.InterruptedException ie) {
		    System.out.println(ie);
		}
		System.out.println("DownloadHelper.downloadVideo -> Getting file from url: " + url + "\n\tto: " + target.getPath());
		FileUtils.copyURLToFile(new URL(url), target);
		
	}
	
	public static void downloadHelpForOpenload(String openloadUrl){
		
	}
	
}

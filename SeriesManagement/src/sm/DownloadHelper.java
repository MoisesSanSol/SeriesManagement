package sm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
	
	public static void downloadHelpForOpenload(HashMap<String,String> openloadUrls, String seriesShort, String seriesFileId, String targetFolder) throws Exception{
		
		File donwnloadLinks = new File(targetFolder + seriesShort + "_OpenloadLinks.html");
		Writer writerHtml = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(donwnloadLinks), "UTF-8"));

		File renameHelper = new File(targetFolder + seriesShort + "_renameHelper.bat");
		Writer writerRename = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(renameHelper), "UTF-8"));
		
		for(String openloadUrl : openloadUrls.keySet()){
		
			String episodeNumber = openloadUrls.get(openloadUrl);
			
			writerHtml.write("<a href='" + openloadUrl + "' target='_blank'>" + seriesShort + "_" + episodeNumber + "</a><br>\r\n");
			writerRename.write("ren \"" + seriesFileId + "_" + episodeNumber + ".mp4\" \"" + seriesShort + "_" + episodeNumber + ".mp4\"\r\n");
		}
		writerHtml.close();
		writerRename.write("del " + seriesShort + "_OpenloadLinks.html");
		writerRename.write("pause");
		writerRename.write("del " + seriesShort + "_renameHelper.bat");
		writerRename.close();

	}
}

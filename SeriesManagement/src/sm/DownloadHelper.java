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
	
	public static void downloadHelpForOpenload(HashMap<String,String> openloadUrls, String seriesShort, String seriesFileId, String targetFolderPath) throws Exception{
		
		LocalConf conf = LocalConf.getInstance();
		String localSeriesFolderPath = conf.downloadTargetFolder.getAbsolutePath() + "/";
		
		File donwnloadLinks = new File(localSeriesFolderPath + seriesShort + "_OpenloadLinks.html");
		Writer writerHtml = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(donwnloadLinks), "UTF-8"));

		File renameHelper = new File(localSeriesFolderPath + seriesShort + "_renameHelper.bat");
		Writer writerRename = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(renameHelper), "UTF-8"));
		
		for(String openloadUrl : openloadUrls.keySet()){
		
			String episodeNumber = openloadUrls.get(openloadUrl);
			String paddedEpisodeNumber = String.format("%02d", Integer.parseInt(episodeNumber));
			writerHtml.write("<a href='" + openloadUrl + "' target='_blank'>" + seriesShort + "_" + episodeNumber + "</a><br>\r\n");
			writerRename.write("ren \"" + seriesFileId + "_" + episodeNumber + ".mp4\" \"" + seriesShort + "_" + paddedEpisodeNumber + ".mp4\"\r\n");
			writerRename.write("pause\r\n");
			writerRename.write("move \"" + seriesShort + "_" + paddedEpisodeNumber + ".mp4\" \"" + targetFolderPath + seriesShort + "_" + paddedEpisodeNumber + ".mp4\"\r\n");
			writerRename.write("pause\r\n");
		}
		writerHtml.close();
		writerRename.write("del " + seriesShort + "_OpenloadLinks.html\r\n");
		writerRename.write("pause\r\n");
		writerRename.write("del " + seriesShort + "_renameHelper.bat\r\n");
		writerRename.close();
	}
}

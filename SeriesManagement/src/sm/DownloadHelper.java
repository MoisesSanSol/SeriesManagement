package sm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
	
	public static void downloadHelpForAlternatives(HashMap<String,String> alternativeUrls, String seriesShort, String targetFolderPath) throws Exception{
		
		LocalConf conf = LocalConf.getInstance();
		String localSeriesFolderPath = conf.downloadTargetFolder.getAbsolutePath() + "/";
		
		File donwnloadLinks = new File(localSeriesFolderPath + seriesShort + "_AlternativeLinks.html");
		Writer writerHtml = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(donwnloadLinks), "UTF-8"));

		File renameHelper = new File(localSeriesFolderPath + seriesShort + "_renameHelper.bat");
		Writer writerRename = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(renameHelper), "UTF-8"));
		
		List<String> keys = new ArrayList<String>(alternativeUrls.keySet());
		Collections.sort(keys);
		
		for(String episodeNumberAndHost : keys){
			
			String seriesFileId = episodeNumberAndHost.split("#")[0];
			String episodeNumber = episodeNumberAndHost.split("#")[1];
			String host = episodeNumberAndHost.split("#")[2];
			String alternativeUrl = alternativeUrls.get(episodeNumberAndHost);
			String paddedEpisodeNumber = String.format("%02d", Integer.parseInt(episodeNumber));
			writerHtml.write("<a href='" + alternativeUrl + "' target='_blank'>" + seriesShort + "_" + episodeNumber + " (" + host + ")</a><br>\r\n");
			writerRename.write("ren \"" + seriesFileId + "_" + episodeNumber + ".mp4\" \"" + seriesShort + "_" + paddedEpisodeNumber + ".mp4\"\r\n");
			writerRename.write("pause\r\n");
			writerRename.write("copy \"" + seriesShort + "_" + paddedEpisodeNumber + ".mp4\" \"" + targetFolderPath + seriesShort + "_" + paddedEpisodeNumber + ".mp4\"\r\n");
			writerRename.write("pause\r\n");
		}
		writerHtml.close();
		writerRename.write("del " + seriesShort + "_AlternativeLinks.html\r\n");
		writerRename.write("pause\r\n");
		writerRename.write("del " + seriesShort + "_renameHelper.bat\r\n");
		writerRename.close();
	}
}

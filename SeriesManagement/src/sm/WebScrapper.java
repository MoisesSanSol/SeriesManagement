package sm;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScrapper {

	
	public static String getFileUrlFromZippyshare(String url) throws Exception{
		
		String fileUrl = "NotFound";
		
		Document doc = Jsoup.connect(url).maxBodySize(0).get();	
		
		System.out.println("Scrapping page: " + url);
		
		String urlBase = url.replaceAll("\\.com\\/.+", ".com");
		
		String pageContent = doc.outerHtml();
		
        String pattern = "document\\.getElementById\\('dlbutton'\\)\\.href = \"(.+?)\" \\+ \\((\\d+) % (\\d+) \\+ (\\d+) % (\\d+)\\) \\+ \"(\\/.+\\.mp4)\";";

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(pageContent);

        if (m.find()) {
           
        	int fileFolder = Integer.parseInt(m.group(2)) %  Integer.parseInt(m.group(3)) +  Integer.parseInt(m.group(4)) %  Integer.parseInt(m.group(5));
            
            fileUrl = urlBase + m.group(1) + fileFolder + m.group(6);
            
            System.out.println("File url from Zippyshare: " + fileUrl);
            
        } else {
            System.out.println("NO MATCH");
        }
		
        return fileUrl;
	}
	
	public static ArrayList<String> getAllEpisodesUrls(Document mainSeriesPage){
		
		ArrayList<String> episodeLinks = new ArrayList<String>();
		
		Elements links = mainSeriesPage.select("li.fa-play-circle");
		
		for(Element link : links){

			String realtiveLinkUrl = link.select("a").first().attr("href");
			if(!realtiveLinkUrl.equals("#")){
				String linkUrl = LocalConf.animeFlvBaseUrl + realtiveLinkUrl;
				episodeLinks.add(linkUrl);
				//System.out.println(link.outerHtml());
			}
		}
		
		Collections.sort(episodeLinks);
		
		return episodeLinks;
	}
	
	public static String getZippyshareUrl(Document episodePage) throws Exception{
		
		String zippyUrl = "";
		
		String redirectLink = episodePage.select("a[href*=zippyshare]").first().attr("href");
		String[] urls = redirectLink.split("=http");
		zippyUrl = "http" +  URLDecoder.decode(urls[1], "UTF-8");
		
		return zippyUrl;
	}
	
	public static String getOpenloadUrl(Document episodePage) throws Exception{
		
		String openloadUrl = "";
		
		String redirectLink = episodePage.select("a[href*=openload]").first().attr("href");
		String[] urls = redirectLink.split("=http");
		openloadUrl = "http" +  URLDecoder.decode(urls[1], "UTF-8");
		
		return openloadUrl;
	}
	
	
	public static String getSeriesId(Document episodePage) throws Exception{
		
		String seriesId = "";
		
		String all = episodePage.select("script:containsData(anime_id)").first().html().replace("\r\n", "");
		seriesId = all.replaceAll(".*?anime_id = (\\d+?);.*$", "$1");
		
		return seriesId;
	}
	
	public static String getSeriesStatus(Document mainSeriesPage) throws Exception{
		
		String seriesStatus = "";
		
		seriesStatus = mainSeriesPage.select(".fa-tv").first().text();
		
		return seriesStatus;
	}
	
}

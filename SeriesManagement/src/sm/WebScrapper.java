package sm;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import conf.LocalConf;

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
	
	public static String getFileUrlFromZippyshareV2(String url) throws Exception{
		
		String fileUrl = "NotFound";

		String urlBase = url.replaceAll("\\.com\\/.+", ".com");
		System.out.println("Scrapping page: " + url);
		
		Document doc = Jsoup.connect(url).maxBodySize(0).get();	

		if(!doc.select("div:contains(File has expired and does not exist anymore on this server)").isEmpty()){
			System.out.println("File not available anymore.");
		}
		else{
			Element downloadAnchor = doc.select("a#dlbutton").first(); 
			Element javascript = downloadAnchor.nextElementSibling();
			
			String[] lines =  javascript.html().split("\n");
			
			//System.out.println(javascript.html());
			
	        String pattern = "var a = (\\d+)%(\\d+);";
	        Pattern p = Pattern.compile(pattern);
	        Matcher m = p.matcher(lines[0]);
	
	        if (m.find()) {
	           
	        	int a = Integer.parseInt(m.group(1)) %  Integer.parseInt(m.group(2));
	        	
	        	//System.out.println(a);
	        	
	        	String pattern2 = "document.getElementById\\('dlbutton'\\).href = \"(.+?)\"\\+a\\+\"(.+?.mp4)\";";
	        	Pattern p2 = Pattern.compile(pattern2);
	            Matcher m2 = p2.matcher(lines[1]);
	        	
	            if (m2.find()) {
	            	
	            	fileUrl = urlBase + m2.group(1) + a + m2.group(2);
	
	            	System.out.println("File url from Zippyshare: " + fileUrl);
	            }
	            else {
	            	System.out.println("Error building url");
	            }
	        }
	        else {
	        	System.out.println("Error building url");
	        }
		}
        return fileUrl;
	}
	
	public static String getFileUrlFromZippyshareV3(String url) throws Exception{
		
		String fileUrl = "NotFound";

		if(!url.equals("NoZippyshareUrl")) {
		
			String urlBase = url.replaceAll("\\.com\\/.+", ".com");
			System.out.println("Scrapping page: " + url);
			
			Document doc = Jsoup.connect(url).maxBodySize(0).validateTLSCertificates(false).get();	
	
			if(!doc.select("div:contains(File has expired and does not exist anymore on this server)").isEmpty()){
				System.out.println("File not available anymore.");
			}
			if(!doc.select("div:contains(File does not exist on this server)").isEmpty()){
				System.out.println("File not available anymore.");
			}
			else{
				Element downloadAnchor = doc.select("a#dlbutton").first(); 
				Element javascript = downloadAnchor.nextElementSibling();
				
				String[] lines =  javascript.html().split("\n");
				
		        String pattern = "document\\.getElementById\\('dlbutton'\\)\\.href = (.+)";
		        Pattern p = Pattern.compile(pattern);
		        Matcher m = p.matcher(lines[0]);
		
		        if (m.find()) {
		        	
		            ScriptEngineManager manager = new ScriptEngineManager();
		            ScriptEngine se = manager.getEngineByName("JavaScript");        
		            
		            try {
		                Object result = se.eval(m.group(1));
		                System.out.println(result.toString());
		            	fileUrl = urlBase + result.toString();
		            	System.out.println("File url from Zippyshare: " + fileUrl);
		            	
		            } catch (Exception e) {
			        	System.out.println("Error building url");
		            }
		        }
		        else {
		        	System.out.println("Error building url");
		        }
			}
		}
		
        return fileUrl;
	}

	public static String getFileUrlFromZippyshareV4(String url) throws Exception{
		
		String fileUrl = "NotFound";
	
		String urlBase = url.replaceAll("\\.com\\/.+", ".com");
		System.out.println("Scrapping page: " + url);
		
		Document doc = Jsoup.connect(url).maxBodySize(0).get();	
	
		//*
		if(!doc.select("div:contains(File has expired and does not exist anymore on this server)").isEmpty()){
			System.out.println("File not available anymore.");
		}
		else{
			Element downloadAnchor = doc.select("a#dlbutton").first(); 
			Element javascript = downloadAnchor.nextElementSibling();
			//System.out.println(javascript.html());
			
			String[] lines =  javascript.html().split("\n");
			
	        int a;
	        Pattern patA = Pattern.compile("var a = (\\d+);");
	        Matcher matA = patA.matcher(lines[0]);
	        int b = 3;
	        //Pattern patB = Pattern.compile("var a = (\\d+);");
	        //Matcher matB = patA.matcher(lines[0]);
	
	        if (matA.find()) {
	           
	        	a = Integer.parseInt(matA.group(1));
	        	//System.out.println(a);
	        	
	        	Pattern patUrl = Pattern.compile("document.getElementById\\('dlbutton'\\).href = \"(.+?)\"\\+\\(Math\\.pow\\(a, 3\\)\\+b\\)\\+\"(.+?.mp4)\";");
	            Matcher matUrl = patUrl.matcher(lines[3]);
	        	
	            if (matUrl.find()) {
	            	
	            	int dynamicUrl = (int)Math.pow(a, 3) + b;
	            	
	            	fileUrl = urlBase + matUrl.group(1) + dynamicUrl + matUrl.group(2);
	
	            	System.out.println("File url from Zippyshare: " + fileUrl);
	            }
	            else {
	            	System.out.println("Error building url.");
	            }
	        }
	        else {
	        	System.out.println("Error building url.");
	        }
		}
		return fileUrl;
	}
	
	public static ArrayList<String> getAllEpisodesUrls(Document mainSeriesPage) throws Exception{
		
		LocalConf conf = LocalConf.getInstance();
		
		ArrayList<String> episodeLinks = new ArrayList<String>();
		
		Elements links = mainSeriesPage.select("li.fa-play-circle");
		
		for(Element link : links){

			String realtiveLinkUrl = link.select("a").first().attr("href");
			if(!realtiveLinkUrl.equals("#")){
				String linkUrl = conf.animeFlvBaseUrl + realtiveLinkUrl;
				episodeLinks.add(linkUrl);
				//System.out.println(link.outerHtml());
			}
		}

		// Try old AFLV list css
		if(episodeLinks.isEmpty()){
			links = mainSeriesPage.select("a.fa-play-circle");
			for(Element link : links){
				String realtiveLinkUrl = link.attr("href");
				if(!realtiveLinkUrl.equals("#")){
					String linkUrl = conf.animeFlvBaseUrl + realtiveLinkUrl;
					episodeLinks.add(linkUrl);
					//System.out.println(link.outerHtml());
				}
			}
		}
		
		// Try new AFLV list css
		if(episodeLinks.isEmpty()){
			
			Elements scripts = mainSeriesPage.select("script");
			for(Element script : scripts){
				//System.out.println(script.outerHtml());
				String html = script.outerHtml();
				if(html.contains("var episodes ")) {
					
					String episodeName = html.split("];")[0].split("\r\n")[1].replaceAll(".+?\\[\".+?\",\".+?\",\"", "").replaceAll("\".*", "");
					String episodesVar = html.split("];")[1].split("\r\n")[1].replaceAll(".+?\\[\\[", "").replaceAll("\\]$", "");
					String[] episodesPairs = episodesVar.split("\\],\\[");
					
					for(String episodePair : episodesPairs){
						
						String linkUrl = conf.animeFlvBaseUrl + "/ver/" + episodePair.split(",")[1] + "/" + episodeName + "-" + episodePair.split(",")[0];
						episodeLinks.add(linkUrl);
						//System.out.println(linkUrl);
					}
				}
			}
		}
		
		Collections.sort(episodeLinks);
		
		return episodeLinks;
	}
	
	public static String getZippyshareUrl(Document episodePage) throws Exception{
		
		String zippyUrl = "NoZippyshareUrl";
		try {
			String redirectLink = episodePage.select("a[href*=zippyshare]").first().attr("href");
			String[] urls = redirectLink.split("=http");
			zippyUrl = "http" +  URLDecoder.decode(urls[1], "UTF-8");
		}
		catch(Exception ex) {
			System.out.println("Page " + episodePage.baseUri() + " has no or broken zippyshare link.");
			Audit.getInstance().addLog("Page " + episodePage.baseUri() + " has no or broken zippyshare link.");
		}
		
		return zippyUrl;
	}
	
	public static String getOpenloadUrl(Document episodePage) throws Exception{
		
		String openloadUrl = "";
		
		String redirectLink = episodePage.select("a[href*=openload]").first().attr("href");
		String[] urls = redirectLink.split("=http");
		openloadUrl = "http" +  URLDecoder.decode(urls[1], "UTF-8");
		
		return openloadUrl;
	}
	
	public static String getMegaUrl(Document episodePage) throws Exception{
		
		String megaUrl = "";
		
		String redirectLink = episodePage.select("a[href*=mega]").first().attr("href");
		String[] urls = redirectLink.split("=http");
		megaUrl = "http" +  URLDecoder.decode(urls[1], "UTF-8");
		
		return megaUrl;
	}
	
	public static String getSeriesStatus(Document mainSeriesPage) throws Exception{
		
		String seriesStatus = "";
		
		seriesStatus = mainSeriesPage.select(".fa-tv").first().text();
		
		return seriesStatus;
	}
	
	public static String getFileUrlFromZippyshareV5(String url) throws Exception{
		
		String fileUrl = "NotFound";
	
		String urlBase = url.replaceAll("\\.com\\/.+", ".com");
		System.out.println("Scrapping page: " + url);
		
		Document doc = Jsoup.connect(url).maxBodySize(0).get();	
		//System.out.println("html: " + doc.html());
		
		if(!doc.select("div:contains(File has expired and does not exist anymore on this server)").isEmpty()){
			System.out.println("File not available anymore.");
		}
		else if(!doc.select("div:contains(File does not exist on this server)").isEmpty()) {
			System.out.println("File not available anymore.");
		}
		else{
			Element downloadAnchor = doc.select("a#dlbutton").first();
			Element omg = downloadAnchor.nextElementSibling();
			Element javascript = omg.nextElementSibling();
			//System.out.println(javascript.html());
			
			String[] lines =  javascript.html().split("\n");

			String thing = "(\\d+)\\Q%1000 + a() + b() + c() + d + 5/5\\E";
			Pattern patUrl = Pattern.compile("document.getElementById\\('dlbutton'\\).href = \"(.+?)\"\\+\\(" + thing + "\\)\\+\"(.+?.mp4)\";");
            Matcher matUrl = patUrl.matcher(lines[5]);
			
            if (matUrl.find()) {

	        	int a = Integer.parseInt(matUrl.group(2));
            	int b = 1 + 2 + 3 + (2*2) + (5/5); 
            	int dynamicUrl = (a%1000) + b;
            	
            	fileUrl = urlBase + matUrl.group(1) + dynamicUrl + matUrl.group(3);
	
            	System.out.println("File url from Zippyshare: " + fileUrl);
	        }
	        else {
	        	System.out.println("Error building url.");
	        }
		}
		return fileUrl;
	}

	public static String getFileUrlFromZippyshareV6(String url) throws Exception{
		
		String fileUrl = "NotFound";
	
		String urlBase = url.replaceAll("\\.com\\/.+", ".com");
		System.out.println("Scrapping page: " + url);
		
		Document doc = Jsoup.connect(url).maxBodySize(0).get();	
		//System.out.println("html: " + doc.html());
		
		if(!doc.select("div:contains(File has expired and does not exist anymore on this server)").isEmpty()){
			System.out.println("File not available anymore.");
		}
		else if(!doc.select("div:contains(File does not exist on this server)").isEmpty()) {
			System.out.println("File not available anymore.");
		}
		else{
			Element downloadAnchor = doc.select("a#dlbutton").first();
			Element omg = downloadAnchor.nextElementSibling();
			Element javascript = omg.nextElementSibling();
			//System.out.println(javascript.html());
			
			String[] lines =  javascript.html().split("\n");

			String thing = "(\\d+)\\Q%1000 + a() + b() + c() + d + 5/5\\E";
			Pattern patUrl = Pattern.compile("document.getElementById\\('dlbutton'\\).href = \"(.+?)\"\\+\\(" + thing + "\\)\\+\"(.+?.mp4)\";");
            Matcher matUrl = patUrl.matcher(lines[5]);
			
            if (matUrl.find()) {

	        	int a = Integer.parseInt(matUrl.group(2));
            	int b = 1 + 2 + 3 + (2*2) + (5/5); 
            	int dynamicUrl = (a%1000) + b;
            	
            	fileUrl = urlBase + matUrl.group(1) + dynamicUrl + matUrl.group(3);
	
            	System.out.println("File url from Zippyshare: " + fileUrl);
	        }
	        else {
	        	System.out.println("Error building url.");
	        }
		}
		return fileUrl;
	}
	
	public static String getFileUrlFromZippyshareV7(String url) throws Exception{
		
		/* Javascript code example:
			<span id="omg" class="2" style="display:none;"></span>
			<script type="text/javascript">
			    var a = function() {return 1};
			    var b = function() {return a() + 1};
			    var c = function() {return b() + 1};
			    var d = document.getElementById('omg').getAttribute('class');
			    if (true) { d = d*2;}
			    document.getElementById('dlbutton').href = "/d/dYJAYKjO/"+(389341%1000 + a() + b() + c() + d + 5/5)+"/3024_2.mp4";
			    if (document.getElementById('fimage')) {
			        document.getElementById('fimage').href = "/i/dYJAYKjO/"+(389341%1000 + a() + b() + c() + d + 5/5)+"/3024_2.mp4";
			    }
			</script>
		 */
		
		String fileUrl = "NotFound";
	
		String urlBase = url.replaceAll("\\.com\\/.+", ".com");
		System.out.println("Scrapping page: " + url);
		
		Document doc = Jsoup.connect(url).maxBodySize(0).get();	
		//System.out.println("html: " + doc.html());
		
		if(!doc.select("div:contains(File has expired and does not exist anymore on this server)").isEmpty()){
			System.out.println("File not available anymore.");
		}
		else if(!doc.select("div:contains(File does not exist on this server)").isEmpty()) {
			System.out.println("File not available anymore.");
		}
		else{
			Element downloadAnchor = doc.select("a#dlbutton").first();
			Element omg = downloadAnchor.nextElementSibling();
			Element javascript = omg.nextElementSibling();
			//System.out.println(javascript.html());
			
			String[] lines =  javascript.html().split("\n");

			String thing = "(\\d+)\\Q%1000 + a() + b() + c() + d + 5/5\\E";
			Pattern patUrl = Pattern.compile("document.getElementById\\('dlbutton'\\).href = \"(.+?)\"\\+\\(" + thing + "\\)\\+\"(.+?.mp4)\";");
            Matcher matUrl = patUrl.matcher(lines[5]);
			
            if (matUrl.find()) {

	        	int a = Integer.parseInt(matUrl.group(2));
            	int b = 1 + 2 + 3 + (2*2) + (5/5); 
            	int dynamicUrl = (a%1000) + b;
            	
            	fileUrl = urlBase + matUrl.group(1) + dynamicUrl + matUrl.group(3);
	
            	System.out.println("File url from Zippyshare: " + fileUrl);
	        }
	        else {
	        	System.out.println("Error building url.");
	        }
		}
		return fileUrl;
	}
	
	public static String getSeriesId(Document episodePage) throws Exception{
		
		String seriesId = "";
		
		String all = episodePage.select("script:containsData(anime_id)").first().html().replace("\r\n", "");
		seriesId = all.replaceAll(".*?anime_id = (\\d+?);.*$", "$1");
		
		return seriesId;
	}
}

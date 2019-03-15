package scrappers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ZippyshareScrapper {

	public static String getFileUrlFromZippyshare(String url) throws Exception{
		
		return ZippyshareScrapper.getFileUrlFromZippyshare_V1(url);
	}
	
	public static void getContentFromZippyshare(String url) throws Exception{
		
		Document doc = Jsoup.connect(url).maxBodySize(0).get();	
		
		//System.out.println("html: ");
		System.out.println(doc.toString());
		
		
		//Element downloadAnchor = doc.select("a#dlbutton").first();
		//System.out.println(downloadAnchor.html());
		//Element omg = downloadAnchor.nextElementSibling();
		//System.out.println(omg.html());
		//Element javascript = omg.nextElementSibling();
		//System.out.println(javascript.html());
	}
	
	public static String getFileUrlFromZippyshare_V1(String url) throws Exception{
		
/*------------------------------------------------------------------------------------------------------
var a = 467485;
    var b = 742589;
    document.getElementById('dlbutton').omg = "f";
    if (document.getElementById('dlbutton').omg != 'f') {
       a = Math.ceil(a/3);
    } else {
       a = Math.floor(a/3);
    }
    document.getElementById('dlbutton').href = "/d/CaVD8SkP/"+(a + 467485%b)+"/1169_7.mp4";
    if (document.getElementById('fimage')) {
        document.getElementById('fimage').href = "/i/CaVD8SkP/"+(a + 467485%b)+"/1169_7.mp4";
    }
------------------------------------------------------------------------------------------------------*/
		
		String fileUrl = "NotFound";
		
		System.out.println("Zippyshare Scrapping V 1");
		System.out.println("Scrapping page: " + url);
		
		String urlBase = url.replaceAll("\\.com\\/.+", ".com");
		
		Document doc = Jsoup.connect(url).maxBodySize(0).get();
		
		if(!doc.select("div:contains(File does not exist on this server)").isEmpty()){
			System.out.println("File not available anymore.");
		}
		else{
			Element downloadAnchor = doc.select("a#dlbutton").first();
			Element javascript = downloadAnchor.nextElementSibling();
			
			String[] lines =  javascript.html().split("\n");
			
			int a;
	        Pattern patA = Pattern.compile("var a = (\\d+);");
	        Matcher matA = patA.matcher(lines[0].trim());
	        int b;
	        Pattern patB = Pattern.compile("var b = (\\d+);");
	        Matcher matB = patB.matcher(lines[1].trim());
	
	        if (matA.find() && matB.find()) {
	           
	        	a = Integer.parseInt(matA.group(1));
	        	a = (int) Math.ceil(a/3);
	        	b = Integer.parseInt(matB.group(1));
	        	//System.out.println(a);
	        	
	        	Pattern patUrl = Pattern.compile("document.getElementById\\('dlbutton'\\).href = \"(.+?)\"\\+\\(a \\+ (\\d+)%b\\)\\+\"(.+?.mp4)\";");
	            Matcher matUrl = patUrl.matcher(lines[8]);
	        	
	            if (matUrl.find()) {
	            	
	            	int c = Integer.parseInt(matUrl.group(2));
	            	
	            	int dynamicUrl = (int)(a + c%b);
	            	
	            	fileUrl = urlBase + matUrl.group(1) + dynamicUrl + matUrl.group(3);
	
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
}

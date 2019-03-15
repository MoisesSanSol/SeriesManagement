package main;

import scrappers.ZippyshareScrapper;
import sm.Audit;
import sm.WebScrapper;

public class Debug {
	
	public static String zippyUrl = "https://www51.zippyshare.com/v/9w8xMvpZ/file.html";
	
	public static void main(String[] args) throws Exception{

		System.out.println("*** Starting ***\n");

		try{
			Debug.test();
		}
		catch(Exception any){
			Audit.getInstance().dump();
			throw any;
		}
		
		System.out.println("*** Finished ***");
	}

	public static void test() throws Exception{
		
		ZippyshareScrapper.getContentFromZippyshare(zippyUrl);
		//String zippyFileUrl = ZippyshareScrapper.getFileUrlFromZippyshare(zippyUrl);
		//System.out.println(zippyFileUrl);
	}
}

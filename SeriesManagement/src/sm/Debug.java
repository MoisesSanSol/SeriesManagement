package sm;

public class Debug {
	
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
		
		WebScrapper.getFileUrlFromZippyshareV5("http://www103.zippyshare.com/v/sNWhXD86/file.html");
		
	}
}

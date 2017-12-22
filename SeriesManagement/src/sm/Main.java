package sm;

public class Main {

	public static void main(String[] args) throws Exception{

		System.out.println("*** Starting ***\n");

		try{
			//LocalConf.getInstance().updateEpisodeTrackingFile_FromScratch();
			//LocalConf.getInstance().updateEpisodeTrackingFile_ManuallyAddedFiles();
			
			Dispatcher.downloadAllOngoingSeries();
			
			Audit.getInstance().dump();
		}
		catch(Exception any){
			Audit.getInstance().dump();
			throw any;
		}
		
		System.out.println("*** Finished ***");
	}

}

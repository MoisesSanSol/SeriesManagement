package sm;

public class Main {

	public static void main(String[] args) throws Exception{

		System.out.println("*** Starting ***\n");

		//LocalConf.getInstance().updateEpisodeTrackingFile_FromScratch();
		//LocalConf.getInstance().updateEpisodeTrackingFile_ManuallyAddedFiles();
		
		Dispatcher.downloadAllOngoingSeries();
				
		Audit.getInstance().dump();
		
		System.out.println("*** Finished ***");
	}

}

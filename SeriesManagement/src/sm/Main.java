package sm;

public class Main {

	public static void main(String[] args) throws Exception{

		System.out.println("*** Starting ***\n");
		
		Dispatcher.downloadAllEpisodes("2620/gabriel-dropout", "Gabriel DropOut");
		//Dispatcher.downloadAllOngoingSeries();
		//Dispatcher.checkAllOngoingSeriesStatus();
		
		Audit.getInstance().dump();
		
		System.out.println("*** Finished ***");
	}

}

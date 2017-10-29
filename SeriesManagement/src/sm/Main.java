package sm;

public class Main {

	public static void main(String[] args) throws Exception{

		System.out.println("*** Starting ***\n");
		
		//Dispatcher.downloadAllEpisodes("", "");
		Dispatcher.downloadAllOngoingSeries();
		
		Audit.getInstance().dump();
		
		System.out.println("*** Finished ***");
	}

}

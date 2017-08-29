package sm;

public class Main {

	public static void main(String[] args) throws Exception{

		System.out.println("*** Starting ***");
		
		//Dispatcher.downloadAllEpisodes("", "");
		Dispatcher.downloadAllOngoingSeries();
		
		System.out.println("*** Finished ***");
	}

}

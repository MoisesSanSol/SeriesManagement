package sm;

public class Main {

	public static void main(String[] args) throws Exception{

		System.out.println("*** Starting ***\n");
		
		Dispatcher.downloadAllEpisodes("2635/rewrite-moon-and-terra", "Rewrite Moon and Terra");
		//Dispatcher.downloadAllOngoingSeries();
		
		System.out.println("*** Finished ***");
	}

}

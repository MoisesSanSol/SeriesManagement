package sm;

public class Main {

	public static void main(String[] args) throws Exception{

		System.out.println("*** Starting ***\n");
		
		Dispatcher.downloadAllEpisodes("3473/kobayashi-san-chi-no-maid-dragon", "Kobayashi-san Chi no Maid Dragon");
		//Dispatcher.downloadAllOngoingSeries();
		
		System.out.println("*** Finished ***");
	}

}

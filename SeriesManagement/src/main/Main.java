package main;

import sm.Audit;
import sm.Dispatcher;

public class Main {

	public static void main(String[] args) throws Exception{

		System.out.println("*** Starting ***\n");

		try{
			
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

package main;

import sm.Audit;
import sm.Dispatcher;

public class Maintenance {
	
	// Check completion status of older series
	public static void main(String[] args) throws Exception{

		System.out.println("*** Maintenance Starting ***\n");

		try{
			
			Dispatcher.cleanFinishedSeries();
			
			Audit.getInstance().dump();
		}
		catch(Exception any){
			Audit.getInstance().dump();
			throw any;
		}
		
		System.out.println("*** Finished ***");
	}
	
}

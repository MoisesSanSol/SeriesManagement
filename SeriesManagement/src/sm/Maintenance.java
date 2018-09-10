package sm;

public class Maintenance {
	
	// Check completion status of older series
	public static void main(String[] args) throws Exception{

		System.out.println("*** Maintenance Starting ***\n");

		try{
			
			// Testing
			
			Audit.getInstance().dump();
		}
		catch(Exception any){
			Audit.getInstance().dump();
			throw any;
		}
		
		System.out.println("*** Finished ***");
	}
	
}

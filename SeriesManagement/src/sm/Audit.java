package sm;

import java.util.ArrayList;

public class Audit {

   private static Audit instance = null;
   
   public ArrayList<String> logs;
   
   private Audit() {
      // Exists only to defeat instantiation.
	   this.logs = new ArrayList<String>();
   }

   public static Audit getInstance() {
      if(instance == null) {
         instance = new Audit();
      }
      return instance;
   }
   
   public void dump(){
	   System.out.println("Audit Summary:\n");
	   for (String log : logs){
		   System.out.println(log);
	   }
	   System.out.println();
   }
   
   public void addLog(String log){
	   logs.add(log);
   }
}

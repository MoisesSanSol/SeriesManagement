package sm;

public class Maintenance {
	
	// Check completion status of older series
	public static void main(String[] args) throws Exception{

		System.out.println("*** Starting ***\n");

		try{
			
			LocalConf conf = LocalConf.getInstance();
			Dispatcher.updateSeriesInfo();
			
			for(Series series : conf.series) {
				if(series.finished) {
					Audit.getInstance().addLog(series.seriesName + " Finalizada: ");
					if(series.episodesAvailable.size() == series.episodesDownloaded.size()) {
						Audit.getInstance().addLog("Todo descargado");
					}
					else {
						Audit.getInstance().addLog("\t!\t Algo hay pendiente.");
					}
				}
			}
			
			Audit.getInstance().dump();
		}
		catch(Exception any){
			Audit.getInstance().dump();
			throw any;
		}
		
		System.out.println("*** Finished ***");
	}
	
}

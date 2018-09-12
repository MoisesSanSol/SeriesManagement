package sm;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ConfHandler {

	public static ArrayList<Series> getFinishedSeriesList() throws Exception{
		
		LocalConf conf = LocalConf.getInstance();
		String finishedSeriesListFilePath = conf.configurationFilesFolderPath + conf.finishedSeriesFileName;
		File finishedSeriesListFile = new File(finishedSeriesListFilePath);
		
		return ConfHandler.readSeriesListFile(finishedSeriesListFile);
	}
	
	public static void setFinishedSeriesList(ArrayList<Series> seriesList) throws Exception{
		
		LocalConf conf = LocalConf.getInstance();
		String finishedSeriesListFilePath = conf.configurationFilesFolderPath + conf.finishedSeriesFileName;
		File finishedSeriesListFile = new File(finishedSeriesListFilePath);
		
		ConfHandler.writeSeriesListFile(seriesList, finishedSeriesListFile);
	}
	
	public static ArrayList<Series> readSeriesListFile(File file) throws Exception{
		
		ArrayList<Series> seriesList = new ArrayList<Series>();
		
		ArrayList<String> fileContent = (ArrayList<String>)Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
		
		for(String entry : fileContent){
				
			Series series = new Series();
			series.seriesName = entry.split("=")[0];
			series.seriesPage = entry.split("=")[1];
			seriesList.add(series);
		}
		
		return seriesList;
	}
	
	public static void writeSeriesListFile(ArrayList<Series> seriesList, File file) throws Exception{
		
		ArrayList<String> fileContent = new ArrayList<String>(); 
		
		Collections.sort(seriesList, new Comparator<Series>(){
		     public int compare(Series o1, Series o2){
		         return o1.seriesName.compareTo(o2.seriesName);
		     }
		});
		
		for(Series series : seriesList){
			
			String entry = series.seriesName + "=" + series.seriesPage;
			fileContent.add(entry);
		}
		
		Files.write(file.toPath(), fileContent, StandardCharsets.UTF_8);
	}
	
	public static void setOngoingSeriesList(ArrayList<Series> seriesList) throws Exception{
		
		LocalConf conf = LocalConf.getInstance();
		String ongoingSeriesListFilePath = conf.configurationFilesFolderPath + conf.ongoingSeriesFileName;
		File ongoingSeriesListFile = new File(ongoingSeriesListFilePath);
		
		ArrayList<String> fileContent = new ArrayList<String>();
		fileContent.add("### Actually Ongoing");
		fileContent.add("");
		
		Collections.sort(seriesList, new Comparator<Series>(){
		     public int compare(Series o1, Series o2){
		         return o1.seriesName.compareTo(o2.seriesName);
		     }
		});
		
		for(Series series : seriesList){
			
			String entry = series.seriesPage + "=" + series.seriesName;
			fileContent.add(entry);
		}
		
		Files.write(ongoingSeriesListFile.toPath(), fileContent, StandardCharsets.UTF_8);
	}
}

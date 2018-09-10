package sm;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Series {

	String folderName;
	String seriesName;
	String seriesPage;
	String seriesFileId;
	
	LinkedHashMap<String, String> episodesAvailable;
	ArrayList<String> episodesDownloaded;
	
	boolean finished;
	boolean completed;
	
	public Series() {
		
		this.episodesAvailable = new LinkedHashMap<String, String>();
		this.episodesDownloaded = new ArrayList<String>();
		
		this.finished = false;
		this.completed = false;
	}
}

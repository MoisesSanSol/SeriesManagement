package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Series {

	public String seriesName;
	public String seriesPage;
	public String seriesFileId;
	public String seriesFileName;
	
	public LinkedHashMap<String, String> episodesAvailable;
	public ArrayList<String> episodesDownloaded;
	
	public boolean finished;
	public boolean completed;
	
	public Series() {
		
		this.episodesAvailable = new LinkedHashMap<String, String>();
		this.episodesDownloaded = new ArrayList<String>();
		
		this.finished = false;
		this.completed = false;
	}
}

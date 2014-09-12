package ru.raiv.simplerssreader.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RssRecord {

	private String guid;
	private String title;
	private String description;
	private String link;
	private Date time=null;
	
	
	private static final String HUMAN_READABLE_FORMAT="dd.MM.yy HH:mm";
	private static final String RSS_INCOMING_FORMAT="EEE, dd MMM yyyy HH:mm:ss Z";
	
	
	private static final SimpleDateFormat writeFormat = new SimpleDateFormat(RSS_INCOMING_FORMAT,Locale.US);
	private static final SimpleDateFormat readFormat = new SimpleDateFormat(HUMAN_READABLE_FORMAT,Locale.US);
	
	
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public long getTime() {
		if(time == null)
		{
			return -1;
		}
		return time.getTime();
	}
	public Date getTimeAsDate() {
		return time;
		
	}
	public String getTimeAsString() {
		if(time==null)
		{
			return "";
		}
		synchronized(readFormat){
			return readFormat.format(time);
		}
	}
	
	public void setTime(long time) {
		this.time = new Date(time);
	}
	public void setTime(String time) {
		
		synchronized(writeFormat){
			try {
				this.time = writeFormat.parse(time);
			} catch (ParseException e) {
				this.time = null;
			}
		}
	}
	
	
}

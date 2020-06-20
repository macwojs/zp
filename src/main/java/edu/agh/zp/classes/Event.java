package edu.agh.zp.classes;

import java.time.LocalDateTime;

public class Event {
	public long id;
	public LocalDateTime start;
	public LocalDateTime end;
	public String title;
	public String url;

	public Event( long id, LocalDateTime start, LocalDateTime end, String title, String url ) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.title = title;
		this.url = url;
	}
}

package edu.agh.zp.classes;

import java.time.LocalDateTime;

public class Event {
	public final long id;
	public final LocalDateTime start;
	public final LocalDateTime end;
	public final String title;
	public final String url;

	public Event( long id, LocalDateTime start, LocalDateTime end, String title, String url ) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.title = title;
		this.url = url;
	}
}

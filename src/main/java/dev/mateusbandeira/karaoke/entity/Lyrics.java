package dev.mateusbandeira.karaoke.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import dev.mateusbandeira.karaoke.view.Views;
import dev.mateusbandeira.karaoke.view.Views.ViewInsert;

public class Lyrics {
	Integer id;
	@JsonView({ Views.class, Views.ViewInsert.class })
	String writers;
	@JsonView({ ViewInsert.class })
	float delay;
	Integer trackId;
	@JsonView({ Views.class, ViewInsert.class })
	List<Line> lines;

	public Lyrics() {
		super();
	}

	public Lyrics(Integer id, String writers, float delay) {
		super();
		this.id = id;
		this.writers = writers;
		this.delay = delay;
	}
	
	public void applyDelay() {
		for(Line line : getLines()) {
			line.startAt += this.delay;
		}
	}

	public Lyrics(List<Line> lines) {
		this();
		this.lines = lines;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWriters() {
		return writers;
	}

	public float getDelay() {
		return delay;
	}

	public Integer getTrackId() {
		return trackId;
	}

	public void setTrackId(Integer trackId) {
		this.trackId = trackId;
	}

	public List<Line> getLines() {
		return lines;
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}

}

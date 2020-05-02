package dev.mateusbandeira.karaoke.entity;

import com.fasterxml.jackson.annotation.JsonView;

import dev.mateusbandeira.karaoke.view.TrackView;

public class Word {
	@JsonView(TrackView.class)
	private String content;
	@JsonView(TrackView.class)
	private Float duration;
	@JsonView(TrackView.class)
	private Float wait;

	public Word() {
		super();
	}

	public Word(String content, Float duration, Float wait) {
		this();
		this.content = content;
		this.duration = duration;
		this.wait = wait;
	}

	public String getContent() {
		return content;
	}

	public Float getDuration() {
		return duration;
	}

	public Float getWait() {
		return wait;
	}
}

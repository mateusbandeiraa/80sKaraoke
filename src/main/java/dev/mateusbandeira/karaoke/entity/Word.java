package dev.mateusbandeira.karaoke.entity;

import com.fasterxml.jackson.annotation.JsonView;

import dev.mateusbandeira.karaoke.view.Views;

public class Word {
	@JsonView({Views.class, Views.ViewInsert.class})
	private String content;
	@JsonView({Views.class, Views.ViewInsert.class})
	private Float duration;
	@JsonView({Views.class, Views.ViewInsert.class})
	private float wait;

	public Word() {
		super();
	}

	public Word(String content, Float duration, float wait) {
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

	public float getWait() {
		return wait;
	}
}

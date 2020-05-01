package dev.mateusbandeira.karaoke.entity;

public class Word {
	private String content;
	private Float duration;
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

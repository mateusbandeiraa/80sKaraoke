package dev.mateusbandeira.karaoke.entity;

import java.util.List;

public class Line {
	Float startAt;
	Float remain;

	List<Word> words;
	
	public Line() {
		super();
	}

	public Line(Float startAt, Float remain, List<Word> words) {
		this();
		this.startAt = startAt;
		this.remain = remain;
		this.words = words;
	}

	public Float getStartAt() {
		return startAt;
	}

	public Float getRemain() {
		return remain;
	}

	public List<Word> getWords() {
		return words;
	}
}

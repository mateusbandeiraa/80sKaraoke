package dev.mateusbandeira.karaoke.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import dev.mateusbandeira.karaoke.view.TrackView;

public class Line {
	@JsonView(TrackView.class)
	Float startAt;
	@JsonView(TrackView.class)
	Float remain;

	@JsonView(TrackView.class)
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

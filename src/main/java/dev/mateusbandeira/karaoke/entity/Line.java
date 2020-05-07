package dev.mateusbandeira.karaoke.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import dev.mateusbandeira.karaoke.view.Views;

public class Line {
	Integer lineId;
	@JsonView({Views.class, Views.ViewInsert.class})
	Float startAt;
	@JsonView({Views.class, Views.ViewInsert.class})
	Float remain;

	@JsonView({Views.class, Views.ViewInsert.class})
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

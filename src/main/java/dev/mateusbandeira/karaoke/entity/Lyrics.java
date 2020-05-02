package dev.mateusbandeira.karaoke.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import dev.mateusbandeira.karaoke.view.TrackView;

public class Lyrics {
	@JsonView({TrackView.class})
	List<Line> lines;

	public Lyrics() {
		super();
	}

	public Lyrics(List<Line> lines) {
		this();
		this.lines = lines;
	}

	public List<Line> getLines() {
		return lines;
	}

}

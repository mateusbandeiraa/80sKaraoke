package dev.mateusbandeira.karaoke.entity;

import java.util.List;

public class Lyrics {
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

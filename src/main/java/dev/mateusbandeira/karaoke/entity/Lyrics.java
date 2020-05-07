package dev.mateusbandeira.karaoke.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import dev.mateusbandeira.karaoke.view.Views;
import dev.mateusbandeira.karaoke.view.Views.ViewInsert;

public class Lyrics {
	Integer id;
	@JsonView({ Views.class, Views.ViewInsert.class })
	String writers;
	@JsonView({ Views.class, ViewInsert.class })
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

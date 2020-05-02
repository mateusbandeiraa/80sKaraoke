package dev.mateusbandeira.karaoke.entity;

import java.io.InputStream;

import com.fasterxml.jackson.annotation.JsonView;

import dev.mateusbandeira.karaoke.view.TrackView;

public class Track {
	@JsonView({ TrackView.class, TrackView.ViewSearch.class })
	Integer trackId;
	@JsonView({ TrackView.class, TrackView.ViewSearch.class })
	String title;
	@JsonView({ TrackView.class, TrackView.ViewSearch.class })
	String artist;
	@JsonView({ TrackView.class, TrackView.ViewSearch.class })
	String year;

	@JsonView({ TrackView.class })
	Lyrics lyrics;

	public Track() {
		super();
	}

	public Track(Integer trackId, String title, String artist, String year, Lyrics lyrics) {
		this();
		this.trackId = trackId;
		this.title = title;
		this.artist = artist;
		this.year = year;
		this.lyrics = lyrics;
	}

	public InputStream getAudioStream() {
		String audioFilename = "track" + this.getTrackId() + ".mp3";
		InputStream audioResourceStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(audioFilename);
		return audioResourceStream;
	}

	public Integer getTrackId() {
		return trackId;
	}

	public void setTrackId(Integer trackId) {
		this.trackId = trackId;
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public String getYear() {
		return year;
	}

	public Lyrics getLyrics() {
		return lyrics;
	}

}

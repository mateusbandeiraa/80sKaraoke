package dev.mateusbandeira.karaoke.persistence;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.mateusbandeira.karaoke.entity.Track;

public class MockedTrackDAO implements TrackDAO {

	@Override
	public Track get(Integer primaryKey) {
		try {
			return new ObjectMapper().readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream("track4.json"), Track.class);
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}

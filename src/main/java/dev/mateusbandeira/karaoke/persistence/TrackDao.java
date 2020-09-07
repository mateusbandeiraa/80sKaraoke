package dev.mateusbandeira.karaoke.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotFoundException;

import dev.mateusbandeira.karaoke.config.CustomJsonProvider;
import dev.mateusbandeira.karaoke.entity.Track;

public class TrackDao extends DAO<Track> {

	@Override
	public Track select(Integer primaryKey) throws IOException {
		String trackFilename = "tracks/json/track" + primaryKey + ".json";
		InputStream trackResourceStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(trackFilename);
		if (trackResourceStream == null) {
			throw new NotFoundException();
		}
		Track track = CustomJsonProvider.mapper.readValue(trackResourceStream, Track.class);
		return track;
	}

	public List<Track> search(String searchTerms) {
		// TODO

		return null;
	}

	public List<Track> selectFeaturedTracks() throws IOException {
		InputStream featuredTracksStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("tracks/featured.json");
		@SuppressWarnings("unchecked")
		ArrayList<Integer> featuredTracksIds = CustomJsonProvider.mapper
				.readValue(featuredTracksStream, ArrayList.class);

		ArrayList<Track> featuredTracks = new ArrayList<>();

		for (Integer trackId : featuredTracksIds) {
			featuredTracks.add(this.select(trackId));
		}

		return featuredTracks;
	}

}

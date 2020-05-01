package dev.mateusbandeira.karaoke.service;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import dev.mateusbandeira.karaoke.entity.Track;
import dev.mateusbandeira.karaoke.persistence.MockedTrackDAO;

@Path("/track")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class TrackService {

	@GET
	@Path("/{trackid}")
	public Track getTrack(@PathParam("trackid") Integer trackId) {
		return new MockedTrackDAO().get(trackId);
	}
	
	@GET
	@Path("/{trackid}/audio")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public InputStream getAudio(@PathParam("trackid") Integer trackId) {
		Track track = new Track();
		track.setTrackId(trackId);
		return track.getAudioStream();
	}
}

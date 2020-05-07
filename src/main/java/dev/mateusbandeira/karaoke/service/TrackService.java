package dev.mateusbandeira.karaoke.service;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonView;

import dev.mateusbandeira.karaoke.entity.Track;
import dev.mateusbandeira.karaoke.persistence.TrackDao;
import dev.mateusbandeira.karaoke.view.Views;

@Path("/track")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf8")
public class TrackService {

	@GET
	@Path("/{trackid}")
	@JsonView(Views.class)
	public Track getTrack(@PathParam("trackid") Integer trackId) {
		Track track = new TrackDao().select(trackId);
		track.getLyrics().applyDelay();
		return track;
	}
//
//	@GET
//	@Path("/{trackid}/audio")
//	@Produces(MediaType.APPLICATION_OCTET_STREAM)
//	public InputStream getAudio(@PathParam("trackid") Integer trackId) {
//		Track track = new Track();
//		track.setTrackId(trackId);
//		return track.getAudioStream();
//	}
//
	@GET
	@Path("/search")
	@JsonView(Views.ViewSearch.class)
	public List<Track> search(@QueryParam("searchterms") String searchTerms,
			@QueryParam("maxresults") Integer maxResults,
			@QueryParam("pagenumber") Integer pageNumber) {
		searchTerms = StringUtils.trimToEmpty(searchTerms);
		if (StringUtils.length(searchTerms) < 3) {
			throw new BadRequestException(Response.status(Status.BAD_REQUEST)
					.entity("The searchterms parameter must have at least 3 characters.").build());
		}
		return new TrackDao().selectByTitle(searchTerms);
	}
	
	@POST
	@JsonView(Views.class)
	public void createTrack(@JsonView(Views.ViewInsert.class) Track track) {
		try {
			new TrackDao().insert(track);
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new InternalServerErrorException();
		}
	}
}

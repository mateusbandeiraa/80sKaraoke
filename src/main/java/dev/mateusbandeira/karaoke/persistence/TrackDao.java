package dev.mateusbandeira.karaoke.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dev.mateusbandeira.karaoke.entity.Track;

public class TrackDao extends DAO<Track> {

	private static void insert(Connection conn, Track track) throws SQLException {
		String sql = "INSERT INTO Tracks (title, artist, trackYear) values (?, ?, ?)";
		PreparedStatement stmt = conn.prepareStatement(sql,
				PreparedStatement.RETURN_GENERATED_KEYS);
		stmt.setString(1, track.getTitle());
		stmt.setString(2, track.getArtist());
		stmt.setString(3, track.getYear());
		stmt.execute();
		ResultSet generatedTrackKey = stmt.getGeneratedKeys();
		generatedTrackKey.next();
		track.setTrackId(generatedTrackKey.getInt(1));
	}

	@Override
	public void insert(Track track) throws SQLException {
		Connection conn;
		try {
			conn = PersistenceManager.getConnection();
		} catch (SQLException ex1) {
			ex1.printStackTrace();
			throw new RuntimeException(ex1);
		}
		try {
			conn.setAutoCommit(false);
	
			// INSERT TRACK
			System.out.println("Inserting track");
			insert(conn, track);

			// INSERT LYRICS
			System.out.println("Inserting lyrics");
			LyricsDao.insertFromTrack(conn, track);

			System.out.println("Committing");
			conn.setAutoCommit(true);
			System.out.println("End");
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			conn.close();
		}
	}

	@Override
	public Track select(Integer primaryKey) {
		Track track = null;
		String sql = "SELECT title, artist, trackYear FROM Tracks WHERE trackId = ?";

		try (Connection conn = PersistenceManager.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, primaryKey);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				track = new Track(primaryKey, rs.getString(1), rs.getString(2), rs.getString(3),
						null);
			}
			if (track != null) {
				track.setLyrics(LyricsDao.select(conn, primaryKey));
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		return track;
	}

	public List<Track> search(String searchTerms) {
		List<Track> searchResults = new ArrayList<>();

		String sql = "SELECT trackId, title, artist, trackYear FROM Tracks WHERE title LIKE ? " + 
				" UNION " + 
				" SELECT trackId, title, artist, trackYear FROM Tracks WHERE artist LIKE ? ";

		try (Connection conn = PersistenceManager.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, "%" + searchTerms + "%");
			stmt.setString(2, "%" + searchTerms + "%");
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				Track track = new Track(resultSet.getInt(1), resultSet.getString(2),
						resultSet.getString(3), resultSet.getString(4), null);
				searchResults.add(track);
			}

		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		return searchResults;
	}
	
	public List<Track> selectFeaturedTracks() {
		List<Track> searchResults = new ArrayList<>();

		String sql = "SELECT t.trackId, t.title, t.artist, t.trackYear FROM Tracks t"
				+ " INNER JOIN featuredTracks ft ON ft.trackId = t.trackId";

		try (Connection conn = PersistenceManager.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				Track track = new Track(resultSet.getInt(1), resultSet.getString(2),
						resultSet.getString(3), resultSet.getString(4), null);
				searchResults.add(track);
			}

		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		return searchResults;
	}

	@Override
	public List<Track> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Track entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Track entity) {
		// TODO Auto-generated method stub

	}

}

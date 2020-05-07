package dev.mateusbandeira.karaoke.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dev.mateusbandeira.karaoke.entity.Track;

public class TrackDao extends DAO<Track> {

	private static void insert(Connection conn, Track track) throws SQLException {
		String sql = "INSERT INTO Tracks (title, lyricsId, artist, trackYear) values (?, ?, ?, ?)";
		PreparedStatement stmt = conn.prepareStatement(sql,
				PreparedStatement.RETURN_GENERATED_KEYS);
		stmt.setString(1, track.getTitle());
		stmt.setInt(2, track.getLyrics().getId());
		stmt.setString(3, track.getArtist());
		stmt.setString(4, track.getYear());
		stmt.execute();
		ResultSet generatedTrackKeys = stmt.getGeneratedKeys();
		generatedTrackKeys.next();
		track.setTrackId(generatedTrackKeys.getInt(1));
	}

	@Override
	public void insert(Track track) throws SQLException {
		Connection conn;
		try {
			conn = DAO.getConnection();
		} catch (SQLException ex1) {
			ex1.printStackTrace();
			throw new RuntimeException(ex1);
		}
		try {
			conn.setAutoCommit(false);

			// INSERT LYRICS
			System.out.println("Inserting lyrics");
			LyricsDao.insert(conn, track.getLyrics());

			// INSERT TRACK
			System.out.println("Inserting track");
			insert(conn, track);

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
		String sql = "SELECT title, artist, trackYear, lyricsId FROM Tracks WHERE trackId = ?";

		try (Connection conn = DAO.getConnection()) {
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

package dev.mateusbandeira.karaoke.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dev.mateusbandeira.karaoke.entity.Lyrics;
import dev.mateusbandeira.karaoke.entity.Track;

public class LyricsDao extends DAO<Lyrics> {

	protected static void insertFromTrack(Connection conn, Track track) throws SQLException {
		Lyrics lyrics = track.getLyrics();
		String sql = "INSERT INTO Lyrics (writers, trackId, delay) values (?, ?, ?)";
		PreparedStatement stmt = conn.prepareStatement(sql,
				PreparedStatement.RETURN_GENERATED_KEYS);
		stmt.setString(1, lyrics.getWriters());
		stmt.setInt(2, track.getTrackId());
		stmt.setFloat(3, lyrics.getDelay());
		stmt.execute();
		ResultSet generatedLyricKeys = stmt.getGeneratedKeys();
		generatedLyricKeys.next();
		lyrics.setId(generatedLyricKeys.getInt(1));
		if (lyrics.getLines() != null && !lyrics.getLines().isEmpty()) {
			System.out.println("Inserting lines");
			LineDao.insertFromLyrics(conn, lyrics);
			System.out.println("Inserting words");
			WordDao.batchInsertFromLyrics(conn, lyrics);
		}
	}

	@Override
	public void insert(Lyrics entity) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Lyrics select(Integer primaryKey) {
		try (Connection conn = PersistenceManager.getConnection()) {
			return select(conn, primaryKey);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected static Lyrics select(Connection conn, Integer primaryKey) throws SQLException {
		Lyrics lyrics = null;
		String sql = "SELECT writers, delay, trackId FROM Lyrics where lyricsId = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, primaryKey);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				lyrics = new Lyrics(primaryKey, rs.getString(1), rs.getFloat(2));
				lyrics.setTrackId(rs.getInt(3));
			}
		}
		if (lyrics != null) {
			lyrics.setLines(LineDao.selectByLyricsId(conn, primaryKey));
		}
		return lyrics;
	}
	
	protected static Lyrics selectFromTrack(Connection conn, Track track) throws SQLException {
		Lyrics lyrics = null;
		String sql = "SELECT lyricsId, writers, delay FROM Lyrics where trackId = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, track.getTrackId());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				lyrics = new Lyrics(rs.getInt(1), rs.getString(2), rs.getFloat(3));
			}
		}
		if (lyrics != null) {
			lyrics.setLines(LineDao.selectByLyricsId(conn, lyrics.getId()));
		}
		return lyrics;
	}

	@Override
	public List<Lyrics> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Lyrics entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Lyrics entity) {
		// TODO Auto-generated method stub

	}

}

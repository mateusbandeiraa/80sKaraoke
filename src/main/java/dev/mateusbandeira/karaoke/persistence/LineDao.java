package dev.mateusbandeira.karaoke.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dev.mateusbandeira.karaoke.entity.Line;
import dev.mateusbandeira.karaoke.entity.Lyrics;

public class LineDao extends DAO<Line> {

	public static void insertFromLyrics(Connection conn, Lyrics lyrics) throws SQLException {
		StringBuilder sb = new StringBuilder(
				"INSERT INTO `Lines` (startAt, remain, lyricsId) values (?, ?, ?)");
		for (int i = 1; i < lyrics.getLines().size(); i++) {
			sb.append(", (?, ?, ?)");
		}
		PreparedStatement stmt = conn.prepareStatement(sb.toString(),
				PreparedStatement.RETURN_GENERATED_KEYS);
		int counter = 1;
		for (int i = 0; i < lyrics.getLines().size(); i++) {
			Line line = lyrics.getLines().get(i);
			stmt.setFloat(counter++, line.getStartAt());
			stmt.setFloat(counter++, line.getRemain());
			stmt.setInt(counter++, lyrics.getId());
		}
		stmt.execute();
		ResultSet generatedLinesKeys = stmt.getGeneratedKeys();
		for (Line line : lyrics.getLines()) {
			generatedLinesKeys.next();
			int generatedId = generatedLinesKeys.getInt(1);
			line.setLineId(generatedId);
		}
	}

	@Override
	public void insert(Line entity) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Line select(Integer primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Line> selectByLyricsId(Integer lyricsId) {
		try {
			return selectByLyricsId(PersistenceManager.getConnection(), lyricsId);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static List<Line> selectByLyricsId(Connection connn, Integer lyricsId) throws SQLException {
		List<Line> lines = new ArrayList<>();
		String sql = "SELECT lineId, startAt, remain FROM `Lines` where lyricsId = ? ORDER BY startAt ASC";

		try (Connection conn = PersistenceManager.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, lyricsId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Line line = new Line(rs.getFloat(2), rs.getFloat(3), null);
				line.setLineId(rs.getInt(1));
				lines.add(line);
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		if (!lines.isEmpty()) {
			WordDao.selectWordsByLines(connn, lines);
		}

		return lines;
	}

	@Override
	public List<Line> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Line entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Line entity) {
		// TODO Auto-generated method stub

	}

}

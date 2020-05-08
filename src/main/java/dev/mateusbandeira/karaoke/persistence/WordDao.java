package dev.mateusbandeira.karaoke.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import dev.mateusbandeira.karaoke.entity.Line;
import dev.mateusbandeira.karaoke.entity.Lyrics;
import dev.mateusbandeira.karaoke.entity.Word;

public class WordDao extends DAO<Word> {

	protected static void batchInsertFromLyrics(Connection conn, Lyrics lyrics)
			throws SQLException {
		StringBuilder sb = new StringBuilder(
				"INSERT INTO Words (lineId, wordOrder, content, duration, wait) values (?, ?, ?, ?, ?)");
		for (int i = 0; i < lyrics.getLines().size(); i++) {
			for (int j = 0; j < lyrics.getLines().get(i).getWords().size(); j++) {
				if (!(i == 0 && j == 0))
					sb.append(", (?, ?, ?, ?, ?)");
			}
		}
		PreparedStatement stmt = conn.prepareStatement(sb.toString());
		int counter = 1;
		for (Line line : lyrics.getLines()) {
			int wordCount = 0;
			for (Word word : line.getWords()) {
				stmt.setInt(counter++, line.getLineId());
				stmt.setInt(counter++, wordCount++);
				stmt.setString(counter++, word.getContent());
				stmt.setFloat(counter++, word.getDuration());
				stmt.setFloat(counter++, word.getWait());
			}
		}
		System.out.println("Adding to batch");
		stmt.execute();
	}

	@Override
	public void insert(Word entity) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Word select(Integer primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	protected static void selectWordsByLines(Connection conn, List<Line> lines)
			throws SQLException {
		StringBuilder sb = new StringBuilder(
				"SELECT lineId, content, duration, wait FROM Words WHERE lineId IN(?");
		for (int i = 1; i < lines.size(); i++) {
			sb.append(", ?");
		}
		sb.append(") ORDER BY lineId ASC, wordOrder ASC");

		PreparedStatement stmt = conn.prepareStatement(sb.toString());
		int counter = 1;
		for (Line line : lines) {
			stmt.setInt(counter++, line.getLineId());
		}

		ResultSet rs = stmt.executeQuery();

		Iterator<Line> linesIterator = lines.iterator();
		Line currentLine = linesIterator.next();
		while (rs.next()) {
			while (currentLine.getLineId().compareTo(rs.getInt(1)) < 0) {
				currentLine = linesIterator.next();
			}
			Word word = new Word(rs.getString(2), rs.getFloat(3), rs.getFloat(4));
			currentLine.getWords().add(word);
		}
	}

	@Override
	public List<Word> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Word entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Word entity) {
		// TODO Auto-generated method stub

	}
}

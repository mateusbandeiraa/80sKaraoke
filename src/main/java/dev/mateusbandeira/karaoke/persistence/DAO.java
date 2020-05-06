package dev.mateusbandeira.karaoke.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public abstract class DAO<T> {

	public abstract void insert(T entity) throws SQLException;

	public abstract T select(Integer primaryKey);

	public abstract List<T> selectAll();

	public abstract void update(T entity);

	public abstract void delete(T entity);

	private static class DBCredentials {
		final String username;
		final String password;
		final String dbUrl;
		final String dbHost;
		final String dbName;

		public DBCredentials(String username, String password, String dbHost, String dbName) {
			this.username = username;
			this.password = password;
			this.dbHost = dbHost;
			this.dbName = dbName;
			this.dbUrl = "jdbc:mysql://" + dbHost + "/" + dbName;
		}

	}

	private static DBCredentials getDBCredentials() throws URISyntaxException {
		URI dbUri = new URI(System.getenv("DATABASE_URL"));
		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];

		return new DBCredentials(username, password, dbUri.getHost(),
				dbUri.getPath().replace("/", ""));
	}

	protected static Connection getConnection() throws SQLException, URISyntaxException {
		DBCredentials credentials = getDBCredentials();
		try {
			return DriverManager.getConnection(credentials.dbUrl, credentials.username,
					credentials.password);
		} catch (SQLException ex) {
			if (ex.getMessage().contains("Unknown database")) {
				System.out.println("Database does not exist. Will try to create one.");
				try {
					restoreSchema();
				} catch (URISyntaxException | IOException | SQLException ex1) {
					System.out.println("Could not create a new schema.");
					throw new RuntimeException(ex1);
				}
				return getConnection();
			}
			throw ex;
		}
	}

	public static void main(String[] args) throws IOException {
		try {
			System.out.println("Trying to get connection.");
			Connection connection = getConnection();
			System.out.println("Success.");
			updateTablesToLatestStructure(connection);
		} catch (SQLException | URISyntaxException ex) {
			ex.printStackTrace();
		}
	}

	private static String[] getSQLFilesList() throws IOException {
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("SQL/");
		String files = IOUtils.toString(stream, StandardCharsets.UTF_8.name());
		return files.split("\n");
	}

	private static String getSQLFromFile(String filename) throws IOException {
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("SQL/" + filename);
		String sqlText = IOUtils.toString(stream, StandardCharsets.UTF_8.name());
		return sqlText;
	}

	private static void runDDLUpdateFromFile(Connection conn, String filename)
			throws IOException, SQLException {
		String sqlFromFile = getSQLFromFile(filename);

		PreparedStatement stmt = conn.prepareStatement(sqlFromFile);
		stmt.executeUpdate();
		stmt.close();

		stmt = conn.prepareStatement("INSERT INTO SchemaChangesLog (ScriptName) values (?)");
		stmt.setString(1, filename);
		stmt.executeUpdate();
		stmt.close();
	}

	private static void restoreSchema() throws URISyntaxException, IOException, SQLException {
		DBCredentials credentials = getDBCredentials();
		String url = "jdbc:mysql://" + credentials.dbHost;
		String databaseName = credentials.dbName;

		try (Connection conn = DriverManager.getConnection(url, credentials.username,
				credentials.password);
				PreparedStatement stmt = conn
						.prepareStatement("DROP DATABASE IF EXISTS " + databaseName);) {
			conn.setAutoCommit(false);

			stmt.addBatch("CREATE DATABASE " + databaseName);
			stmt.executeBatch();

			conn.setAutoCommit(true);
		} catch (SQLException ex) {
			throw ex;
		}
	}

	private static void updateTablesToLatestStructure(Connection conn)
			throws SQLException, IOException, URISyntaxException {
		System.out.println("Started checking if table structure are up to date.");
		PreparedStatement stmt = conn.prepareStatement("SHOW TABLES");
		ResultSet resultSet = stmt.executeQuery();

		boolean schemaChangesLogExists = false;
		while (resultSet.next()) {
			if (resultSet.getString(1).equals("SchemaChangesLog")) {
				schemaChangesLogExists = true;
			}
		}
		resultSet.close();

		if (!schemaChangesLogExists) {
			System.out.println(
					"Did not find the SchemaChangesLog table. Will try to run the file 1.0.0.0-schemachangeslog-baseline.sql now.");
			runDDLUpdateFromFile(conn, "1.0.0.0-schemachangeslog-baseline.sql");
			System.out.println("Ran 1.0.0.0-schemachangeslog-baseline.sql successfully.");
		}

		resultSet = stmt.executeQuery("SELECT ScriptName FROM SchemaChangesLog");

		List<String> executedScriptFiles = new ArrayList<>();
		while (resultSet.next()) {
			executedScriptFiles.add(resultSet.getString(1));
		}

		String[] sqlFilesList = getSQLFilesList();

		for (String sqlFileName : sqlFilesList) {
			if (!executedScriptFiles.contains(sqlFileName)) {
				System.out.println("File " + sqlFileName
						+ " was not executed before. Will try to run it now.");
				runDDLUpdateFromFile(conn, sqlFileName);
				System.out.println("Ran " + sqlFileName + " successfully.");
			}
		}

		System.out.println("Finished updating tables to latest structure.");
	}
}

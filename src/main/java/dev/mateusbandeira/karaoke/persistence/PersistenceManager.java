package dev.mateusbandeira.karaoke.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.IOUtils;

public class PersistenceManager implements ServletContextListener {

	ServletContextEvent sce;
	private final String SQL_RESOURCES_PATH = "/WEB-INF/classes/sql/";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		this.sce = sce;
		try (Connection conn = DAO.getConnection()) {
			updateTablesToLatestStructure(conn);
		} catch (SQLException | URISyntaxException | IOException | ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		this.sce = null;
	}

	protected static void restoreSchema() throws URISyntaxException, IOException, SQLException {
		DAO.DBCredentials credentials = DAO.getDBCredentials();
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

	private void updateTablesToLatestStructure(Connection conn)
			throws SQLException, IOException, URISyntaxException {
		System.out.println("Started checking if table structure are up to date.");
		PreparedStatement stmt = conn.prepareStatement("SHOW TABLES");
		ResultSet resultSet = stmt.executeQuery();

		boolean schemaChangesLogExists = false;
		while (resultSet.next()) {
			if (resultSet.getString(1).equalsIgnoreCase("SchemaChangesLog")) {
				schemaChangesLogExists = true;
			}
		}
		resultSet.close();

		if (!schemaChangesLogExists) {
			System.out.println(
					"Did not find the SchemaChangesLog table. Will try to run the file 1.0.0.0-schemachangeslog-baseline.sql now.");
			runUpdateFromFile(conn, "1.0.0.0-schemachangeslog-baseline.sql");
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
				runUpdateFromFile(conn, sqlFileName);
				System.out.println("Ran " + sqlFileName + " successfully.");
			}
		}

		System.out.println("Finished updating tables to latest structure.");
	}

	private void runUpdateFromFile(Connection conn, String filename)
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

	private String[] getSQLFilesList() throws IOException {
		Set<String> resourcePaths = sce.getServletContext().getResourcePaths(SQL_RESOURCES_PATH);
		String[] filenames = new String[resourcePaths.size()];
		int index = 0;
		for (Iterator<String> iterator = resourcePaths.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			filenames[index++] = string.replace(SQL_RESOURCES_PATH, "");
		}
		return filenames;
	}

	private String getSQLFromFile(String filename) throws IOException {
		InputStream stream = sce.getServletContext().getResourceAsStream(SQL_RESOURCES_PATH + filename);
		String sqlText = IOUtils.toString(stream, StandardCharsets.UTF_8.name());
		return sqlText;
	}

}

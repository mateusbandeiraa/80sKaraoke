package dev.mateusbandeira.karaoke.persistence;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public abstract class DAO<T> {

	public abstract void insert(T entity) throws SQLException;

	public abstract T select(Integer primaryKey);

	public abstract List<T> selectAll();

	public abstract void update(T entity);

	public abstract void delete(T entity);

	static class DBCredentials {
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

	protected static DBCredentials getDBCredentials() throws URISyntaxException {
		URI dbUri = new URI(System.getenv("DATABASE_URL"));
		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];

		return new DBCredentials(username, password, dbUri.getHost(),
				dbUri.getPath().replace("/", ""));
	}

	protected static Connection getConnection() throws SQLException {
		try {
			DBCredentials credentials = getDBCredentials();
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection(credentials.dbUrl, credentials.username,
					credentials.password);
		} catch (SQLException ex) {
			if (ex.getMessage().contains("Unknown database")) {
				System.out.println("Database does not exist. Will try to create one.");
				try {
					PersistenceManager.restoreSchema();
				} catch (URISyntaxException | SQLException ex1) {
					System.out.println("Could not create a new schema.");
					throw new RuntimeException(ex1);
				}
				return getConnection();
			}
			throw ex;
		} catch (ClassNotFoundException | URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
	}
}

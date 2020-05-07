package dev.mateusbandeira.karaoke.persistence;

import java.sql.SQLException;
import java.util.List;

public abstract class DAO<T> {

	public abstract void insert(T entity) throws SQLException;

	public abstract T select(Integer primaryKey);

	public abstract List<T> selectAll();

	public abstract void update(T entity);

	public abstract void delete(T entity);

}

package dev.mateusbandeira.karaoke.persistence;

public abstract interface DAO<T> {
	public T get(Integer primaryKey);
}

package dev.mateusbandeira.karaoke.persistence;

import java.io.IOException;

public abstract class DAO<T> {

	public abstract T select(Integer primaryKey) throws IOException;

}

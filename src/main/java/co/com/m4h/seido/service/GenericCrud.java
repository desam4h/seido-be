package co.com.m4h.seido.service;

import java.util.Optional;

import org.apache.commons.lang.NotImplementedException;

import co.com.m4h.seido.model.AbstractEntity;

public interface GenericCrud<T extends AbstractEntity> {
	default T save(T entity) {
		throw new NotImplementedException();
	}

	default Iterable<T> save(Iterable<T> entities) {
		throw new NotImplementedException();
	}

	Optional<T> find(Long entityId);

	default T update(T entity) {
		return save(entity);
	}

	void delete(Long entityId);
}

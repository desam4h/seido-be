package co.com.m4h.seido.service;

import java.util.List;
import java.util.Optional;

import co.com.m4h.seido.model.Event;

/**
 * Created by hernan on 7/2/17.
 */
public interface EventService extends GenericCrud<Event> {
	List<Event> findAllByPatientId(Long patientId);

	Event save(Long patientId, Event event);

	Event update(Long patientId, Event event);

	void deleteAllByPatientId(Long patientId);

	Optional<Event> findByLoadedId(Long patientId, String loadedId);
}
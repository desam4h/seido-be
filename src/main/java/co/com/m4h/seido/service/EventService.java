package co.com.m4h.registros.service;

import java.util.List;

import co.com.m4h.registros.model.Event;

/**
 * Created by hernan on 7/2/17.
 */
public interface EventService extends GenericCrud<Event> {
	List<Event> findAllByPatientId(Long patientId);

	Event save(Long patientId, Event event);

	Event update(Long patientId, Event event);

	void deleteAllByPatientId(Long patientId);
}
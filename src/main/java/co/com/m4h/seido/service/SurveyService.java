package co.com.m4h.seido.service;

import java.util.List;

import co.com.m4h.seido.model.Survey;

/**
 * Created by hernan on 7/2/17.
 */
public interface SurveyService extends GenericCrud<Survey> {
	List<Survey> findAllByPatient(Long patientId);

	void deleteAllByEventId(Long eventId);

	void deleteAllByPatientId(Long patientId);
}
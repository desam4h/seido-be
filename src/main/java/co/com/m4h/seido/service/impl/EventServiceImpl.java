package co.com.m4h.seido.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.com.m4h.seido.model.Event;
import co.com.m4h.seido.model.Patient;
import co.com.m4h.seido.model.Survey;
import co.com.m4h.seido.model.SurveyType;
import co.com.m4h.seido.persistence.EventRepository;
import co.com.m4h.seido.service.EventService;
import co.com.m4h.seido.service.PatientService;
import co.com.m4h.seido.service.SurveyService;
import co.com.m4h.seido.service.SurveyTemplateService;

/**
 * Created by hernan on 7/2/17.
 */
@Service
public class EventServiceImpl implements EventService {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private PatientService patientService;

	@Autowired
	private SurveyTemplateService templateService;

	@Autowired
	private SurveyService surveyService;

	@Transactional
	@Override
	public Event save(Long patientId, Event event) {
		Patient patient = findEventOwner(patientId);
		event.setPatient(patient);
		Event persistedEvent = eventRepository.save(event);

		List<Survey> surveys = templateService.findAllBySpecialtyId(event.getSpecialty().getId()).stream()
				.filter(template -> template.getType() != SurveyType.BASIC_INFO)
				.map(template -> new Survey(template, persistedEvent)).collect(Collectors.toList());
		surveyService.save(surveys);
		return persistedEvent;
	}

	@Override
	public Event update(Long patientId, Event event) {
		Patient patient = findEventOwner(patientId);
		event.setPatient(patient);
		return eventRepository.save(event);
	}

	@Override
	public Optional<Event> find(Long eventId) {
		return Optional.ofNullable(eventRepository.findOne(eventId));
	}

	@Override
	@Transactional
	public void delete(Long eventId) {
		surveyService.deleteAllByEventId(eventId);
		eventRepository.delete(eventId);
	}

	@Override
	public void deleteAllByPatientId(Long patientId) {
		surveyService.deleteAllByPatientId(patientId);
		eventRepository.deleteAllByPatientId(patientId);
	}

	@Override
	public List<Event> findAllByPatientId(Long patientId) {
		return eventRepository.findAllByOrderByPatientIdAsc(patientId);
	}

	private Patient findEventOwner(Long patientId) {
		return patientService.find(patientId)
				.orElseThrow(() -> new IllegalArgumentException("Patient with doesn't exist"));
	}

	@Override
	public Optional<Event> findByLoadedId(Long patientId, String loadedId) {
		return Optional.ofNullable(eventRepository.findOneByPatientIdAndLoadedId(patientId, loadedId));
	}
}

package co.com.m4h.seido.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.com.m4h.seido.common.SecurityUtil;
import co.com.m4h.seido.model.Company;
import co.com.m4h.seido.model.Patient;
import co.com.m4h.seido.model.Survey;
import co.com.m4h.seido.model.SurveyType;
import co.com.m4h.seido.persistence.PatientRepository;
import co.com.m4h.seido.persistence.SurveyTemplateRepository;
import co.com.m4h.seido.service.EventService;
import co.com.m4h.seido.service.PatientService;
import co.com.m4h.seido.service.SurveyService;

/**
 * Created by hernan on 7/2/17.
 */
@Service
public class PatientServiceImpl implements PatientService {

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private SurveyTemplateRepository templateRepository;

	@Autowired
	private SurveyService surveyService;

	@Autowired
	private EventService eventService;

	@Transactional
	@Override
	public Patient save(Patient patient) {

		// TODO: asignar empresa desde vista

		Company company = SecurityUtil.getCompany();
		patient.setCompany(company);

		final Patient persistedPatient = patientRepository.save(patient);

		// TODO: This could be done in async way - Just a future improve
		// All surveys configured for all Specialties with BASIC_INFO are created
		// All other surveys will be related when related events will be created

		List<Survey> basicSurveys = templateRepository.findAllByCompanyId(company.getId(), SurveyType.BASIC_INFO)
				.stream().map(template -> new Survey(template, persistedPatient)).collect(Collectors.toList());
		surveyService.save(basicSurveys);

		return persistedPatient;
	}

	@Override
	public Patient update(Patient patient) {
		return patientRepository.save(patient);
	}

	@Override
	public Optional<Patient> find(Long patientId) {
		return Optional.ofNullable(patientRepository.findOne(patientId));
	}

	@Override
	@Transactional
	public void delete(Long patientId) {
		// TODO: Add security check against the company

		eventService.deleteAllByPatientId(patientId);
		patientRepository.delete(patientId);
	}

	@Override
	public List<Patient> findAll() {
		Long companyId = SecurityUtil.getCompanyId();
		return patientRepository.findAllByCompanyId(companyId);
	}

	@Override
	public Optional<Patient> findByNuip(String nuip) {
		String role = SecurityUtil.getRole();

		if (role.equals("ROLE_ROOT")) {
			Patient p = patientRepository.findOneByNuip(nuip);

			return Optional.ofNullable(p);
		} else {
			Long companyId = SecurityUtil.getCompanyId();
			Patient p = patientRepository.findOneByCompanyIdAndNuip(companyId, nuip);

			return Optional.ofNullable(p);
		}
	}
}

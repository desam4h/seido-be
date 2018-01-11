package co.com.m4h.registros.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.m4h.registros.common.SecurityUtil;
import co.com.m4h.registros.model.Specialty;
import co.com.m4h.registros.model.SurveyTemplate;
import co.com.m4h.registros.persistence.SpecialtyRepository;
import co.com.m4h.registros.persistence.SurveyTemplateRepository;
import co.com.m4h.registros.service.SurveyTemplateService;

/**
 * Created by hernan on 7/2/17.
 */
@Service
public class SurveyTemplateServiceImpl implements SurveyTemplateService {

	@Autowired
	private SpecialtyRepository specialtyRepository;

	@Autowired
	private SurveyTemplateRepository surveyTemplateRepository;

	@Override
	public List<SurveyTemplate> findAllBySpecialtyId(Long specialtyId) {
		return surveyTemplateRepository.findAllBySpecialtyId(specialtyId);
	}

	@Override
	public SurveyTemplate save(SurveyTemplate surveyTemplate, Long specialtyId) {
		Specialty specialty = specialtyRepository.findOne(specialtyId);

		Long companyId = SecurityUtil.getCompanyId();

		if (!specialty.getCompany().getId().equals(companyId)) {
			throw new IllegalArgumentException("Forbidden company to add specialty");
		} else {
			surveyTemplate.setSpecialty(specialty);
			return surveyTemplateRepository.save(surveyTemplate);
		}
	}

	@Override
	public SurveyTemplate update(SurveyTemplate surveyTemplate, Long specialtyId) {

		// TODO: When model is modified the surveys could change to NOT SOLVED
		// Maybe that could be handled with a PATCH ???

		return save(surveyTemplate, specialtyId);
	}

	@Override
	public Optional<SurveyTemplate> find(Long surveyTemplateId) {
		return Optional.ofNullable(surveyTemplateRepository.findOne(surveyTemplateId));
	}

	@Override
	public void delete(Long surveyTemplateId) {
		surveyTemplateRepository.delete(surveyTemplateId);
	}
}

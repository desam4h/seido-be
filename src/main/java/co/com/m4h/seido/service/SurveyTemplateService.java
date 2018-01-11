package co.com.m4h.registros.service;

import java.util.List;

import co.com.m4h.registros.model.SurveyTemplate;

/**
 * Created by hernan on 7/2/17.
 */
public interface SurveyTemplateService extends GenericCrud<SurveyTemplate> {
	List<SurveyTemplate> findAllBySpecialtyId(Long specialtyId);

	SurveyTemplate save(SurveyTemplate surveyTemplate, Long specialtyId);

	SurveyTemplate update(SurveyTemplate surveyTemplate, Long specialtyId);
}
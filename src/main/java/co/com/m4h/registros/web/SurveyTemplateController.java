package co.com.m4h.registros.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.com.m4h.registros.common.Constant;
import co.com.m4h.registros.model.SurveyTemplate;
import co.com.m4h.registros.service.SurveyTemplateService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by hernan on 7/2/17.
 */
@Slf4j
@RestController
@RequestMapping(value = "/specialty/{specialtyId}/surveyTemplate", produces = Constant.CONTENT_TYPE_JSON)
public class SurveyTemplateController {
	// consumes = Constant.CONTENT_TYPE_JSON,

	private static final String SPECIALTY_ID_PARAM = "specialtyId";

	@Autowired
	private SurveyTemplateService surveyTemplateService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<SurveyTemplate>> findAll(@PathVariable(SPECIALTY_ID_PARAM) Long specialtyId) {
		List<SurveyTemplate> surveys = surveyTemplateService.findAllBySpecialtyId(specialtyId);
		return new ResponseEntity<>(surveys, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<SurveyTemplate> save(@PathVariable(SPECIALTY_ID_PARAM) Long specialtyId,
			@RequestBody SurveyTemplate surveyTemplate) {
		SurveyTemplate persistedSurveyTemplate = surveyTemplateService.save(surveyTemplate, specialtyId);
		return new ResponseEntity<>(persistedSurveyTemplate, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<SurveyTemplate> update(@PathVariable(SPECIALTY_ID_PARAM) Long specialtyId,
			@RequestBody SurveyTemplate surveyTemplate) {
		SurveyTemplate persistedSurveyTemplate = surveyTemplateService.update(surveyTemplate, specialtyId);
		return new ResponseEntity<>(persistedSurveyTemplate, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{surveyTemplateId}", method = RequestMethod.DELETE)
	public ResponseEntity<SurveyTemplate> delete(@PathVariable("surveyTemplateId") Long surveyTemplateId) {
		try {
			surveyTemplateService.delete(surveyTemplateId);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);

		} catch (DataIntegrityViolationException e) {
			log.warn(e.getMessage());
			return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);

		} catch (Exception e) {
			log.warn(e.getMessage());
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
}
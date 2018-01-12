package co.com.m4h.seido.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.com.m4h.seido.common.Constant;
import co.com.m4h.seido.model.Survey;
import co.com.m4h.seido.service.SurveyService;

/**
 * Created by hernan on 7/2/17.
 */
@RestController
@RequestMapping(value = "/patient/{patientId}/survey", produces = Constant.CONTENT_TYPE_JSON)
public class SurveyController {
	// consumes = Constant.CONTENT_TYPE_JSON,

	private static final String PATIENT_ID_PARAM = "patientId";

	private static final String SURVEY_ID_PARAM = "surveyId";

	@Autowired
	private SurveyService surveyService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Survey>> findAll(@PathVariable(PATIENT_ID_PARAM) Long patientId) {
		List<Survey> surveys = surveyService.findAllByPatient(patientId);
		return new ResponseEntity<>(surveys, HttpStatus.OK);
	}

	@RequestMapping(value = "/{surveyId}", method = RequestMethod.GET)
	public ResponseEntity<Survey> find(@PathVariable(PATIENT_ID_PARAM) Long patientId,
			@PathVariable(SURVEY_ID_PARAM) Long surveyId) {
		return surveyService.find(surveyId).map(survey -> new ResponseEntity<>(survey, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<Survey> update(@PathVariable(PATIENT_ID_PARAM) Long patientId, @RequestBody Survey survey) {
		Survey persistedSurvey = surveyService.update(survey);
		return new ResponseEntity<>(persistedSurvey, HttpStatus.CREATED);
	}
}
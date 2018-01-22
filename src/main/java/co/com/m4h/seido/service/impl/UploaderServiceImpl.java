package co.com.m4h.seido.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.MappingIterator;

import co.com.m4h.seido.common.SurveyUtils;
import co.com.m4h.seido.json.SurveyJs;
import co.com.m4h.seido.model.Event;
import co.com.m4h.seido.model.Patient;
import co.com.m4h.seido.model.Specialty;
import co.com.m4h.seido.model.Survey;
import co.com.m4h.seido.model.SurveyTemplate;
import co.com.m4h.seido.model.SurveyType;
import co.com.m4h.seido.service.EventService;
import co.com.m4h.seido.service.PatientService;
import co.com.m4h.seido.service.SurveyService;
import co.com.m4h.seido.service.SurveyTemplateService;
import co.com.m4h.seido.service.UploaderService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UploaderServiceImpl implements UploaderService {
	private static final int START_COLUMN_INDEX = 0;
	@Autowired
	private SurveyService surveyService;
	@Autowired
	private SurveyTemplateService templateService;
	@Autowired
	private PatientService patientService;
	@Autowired
	private EventService eventService;

	@Override
	public int uploadInfo(Long templateId, String info) {
		SurveyTemplate template = this.findTemplate(templateId);
		Set<String> questions = getQuestionsFromModel(template);
		int counter = 0;

		try (MappingIterator<String[]> reader = SurveyUtils.readAnswersFromCSV(questions, info)) {
			while (reader.hasNextValue()) {
				String[] columns = reader.nextValue();
				log.info(":::: " + Arrays.toString(columns));
				List<String> columnList = new ArrayList<>(Arrays.asList(columns));
				String patientNuip = columnList.remove(START_COLUMN_INDEX);
				String eventLoadedId = columnList.remove(START_COLUMN_INDEX);
				validateQuestionsVsAnswers(questions, columnList);

				Patient patient = findPatient(patientNuip);
				Event event = findEvent(patient.getId(), eventLoadedId, template);
				String surveyAnswer = SurveyUtils.transformQuestionsAndAnswersToJson(questions, columnList);
				Optional<Survey> optionalSurvey;

				if (SurveyType.BASIC_INFO == template.getType()) {
					optionalSurvey = surveyService.findByPatientIdAndTemplateId(patient.getId(), template.getId());
				} else {
					optionalSurvey = surveyService.findByEventIdAndTemplateId(event.getId(), template.getId());
				}

				optionalSurvey.ifPresent(survey -> {
					survey.setSurveyAnswers(surveyAnswer);
					surveyService.update(survey);
				});

				counter++;
			}
		} catch (IOException e) {
			log.error("::: Error reading from CSV", e);
		}

		return counter;
	}

	private SurveyTemplate findTemplate(Long templateId) {
		return templateService.find(templateId)
				.orElseThrow(() -> new IllegalArgumentException("::: Template not found"));
	}

	private Patient findPatient(String patientNuip) {
		return patientService.findByNuip(patientNuip).orElseGet(() -> this.buildPatient(patientNuip));
	}

	private Event findEvent(Long patientId, String eventLoadedId, SurveyTemplate template) {
		return eventService.findByLoadedId(patientId, eventLoadedId)
				.orElseGet(() -> this.buildEvent(eventLoadedId, patientId, template.getSpecialty()));
	}

	private Patient buildPatient(String patientNuip) {
		Patient patient = new Patient();
		patient.setNuip(patientNuip);
		return patientService.save(patient);
	}

	private Event buildEvent(String eventLoadedId, Long patientId, Specialty specialty) {
		Event event = new Event();
		event.setCreatedDate(LocalDate.now());
		event.setName("Event #" + eventLoadedId);
		event.setSpecialty(specialty);
		event.setLoadedId(eventLoadedId);
		return eventService.save(patientId, event);
	}

	private Set<String> getQuestionsFromModel(SurveyTemplate template) {
		SurveyJs model = SurveyUtils.parseSurveyModel(template.getJsSurvey());
		return SurveyUtils.getQuestionNamesFromSurveyModel(model);
	}

	private void validateQuestionsVsAnswers(Set<String> questions, List<String> columns) {
		if (questions.size() != columns.size()) {
			throw new IllegalArgumentException("::: Uploaded info doesn't match the model (#questions)");
		}
	}

}

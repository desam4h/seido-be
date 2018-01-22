package co.com.m4h.seido.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.com.m4h.seido.common.SurveyUtils;
import co.com.m4h.seido.json.SurveyJs;
import co.com.m4h.seido.model.Survey;
import co.com.m4h.seido.model.SurveyState;
import co.com.m4h.seido.model.SurveyStatistics;
import co.com.m4h.seido.model.SurveyTemplate;
import co.com.m4h.seido.persistence.SurveyRepository;
import co.com.m4h.seido.persistence.SurveyStatisticRepository;
import co.com.m4h.seido.persistence.SurveyTemplateRepository;
import co.com.m4h.seido.service.SurveyService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by hernan on 7/2/17.
 */
@Service
@Slf4j
public class SurveyServiceImpl implements SurveyService {

	@Autowired
	private SurveyTemplateRepository surveyTemplateRepository;

	@Autowired
	private SurveyRepository surveyRepository;

	@Autowired
	private SurveyStatisticRepository statisticRepository;

	@Override
	public List<Survey> findAllByPatient(Long patientId) {
		return surveyRepository.findAllByPatientId(patientId);
	}

	@Override
	public Survey save(Survey entity) {
		return surveyRepository.save(entity);
	}

	@Override
	public Iterable<Survey> save(Iterable<Survey> entities) {
		return surveyRepository.save(entities);
	}

	@Transactional
	@Override
	public Survey update(Survey survey) {
		// First we validate the answers provided are valid json
		Map<String, Object> answers = SurveyUtils.parseSurveyAnswers(survey.getSurveyAnswers());
		SurveyJs surveyModel = SurveyUtils.parseSurveyModel(survey.getTemplate().getJsSurvey());

		// Then the survey should be updated
		Survey persistedSurvey = find((survey.getId()))
				.orElseThrow(() -> new IllegalArgumentException("Survey not found on update"));
		persistedSurvey.setSurveyAnswers(survey.getSurveyAnswers());
		persistedSurvey.setState(
				SurveyUtils.isSurveyFinished(surveyModel, answers) ? SurveyState.FINISHED : SurveyState.STARTED);

		// TODO: Then a statistic for the survey should be generated (Maybe this could
		// be executed in async way)
		Long companyId = persistedSurvey.getPatient().getCompany().getId();
		Long eventId = Optional.ofNullable(persistedSurvey.getEvent()).map(event -> event.getId()).orElse(null);

		// Then the object with the answers ready to be query is persisted
		SurveyStatistics statistics = SurveyStatistics.builder().surveyId(persistedSurvey.getId()).companyId(companyId)
				.eventId(eventId).patientId(persistedSurvey.getPatient().getId())
				.specialtyId(persistedSurvey.getTemplate().getSpecialty().getId())
				.templateId(persistedSurvey.getTemplate().getId())
				.surveyAnswersCsv(SurveyUtils.formatAnswersAsCSV(answers, false)).build();
		statisticRepository.save(statistics);
		return save(persistedSurvey);
	}

	@Override
	public Optional<Survey> find(Long surveyId) {
		return Optional.ofNullable(surveyRepository.findOne(surveyId));
	}

	@Override
	public Optional<Survey> findByEventIdAndTemplateId(Long eventId, Long templateId) {
		return Optional.ofNullable(surveyRepository.findByEventIdAndTemplateId(eventId, templateId));
	}

	@Override
	public Optional<Survey> findByPatientIdAndTemplateId(Long patientId, Long templateId) {
		return Optional.ofNullable(surveyRepository.findByPatientIdAndTemplateId(patientId, templateId));
	}

	@Override
	public void delete(Long surveyId) {
		surveyRepository.delete(surveyId);
	}

	@Override
	@Transactional
	public void deleteAllByEventId(Long eventId) {
		statisticRepository.deleteAllByEventId(eventId);
		surveyRepository.deleteAllByEventId(eventId);
	}

	@Override
	@Transactional
	public void deleteAllByPatientId(Long patientId) {
		statisticRepository.deleteAllByPatientId(patientId);
		surveyRepository.deleteAllByPatientId(patientId);
	}

	@Override
	@Transactional(readOnly = true)
	public String getStatistics(Long templateId) {
		StringBuilder csvInfo = new StringBuilder();
		Set<String> questionNames = getTemplateQuestionNames(templateId);
		String NEW_LINE = "\n";
		csvInfo.append(transformSurveyAnswersToCSV(questionNames, new HashMap<>(), true));
		csvInfo.append(NEW_LINE);

		try (Stream<Survey> surveyStream = surveyRepository.findAllByTemplateId(templateId)) {
			surveyStream.map(Survey::getSurveyAnswers).map(SurveyUtils::parseSurveyAnswers)
					.map(surveyAnswersMap -> transformSurveyAnswersToCSV(questionNames, surveyAnswersMap, false))
					.forEach(surveyInfo -> {
						csvInfo.append(surveyInfo);
						csvInfo.append(NEW_LINE);
					});
		} catch (Exception e) {
			log.error("::: Error transforming surveys to CSV format ", e);
		}

		return csvInfo.toString();
	}

	/**
	 * Gets the question names from the template model.
	 * 
	 * @param templateId
	 *            Template identifier
	 * @return Set of names in orden from the template model.
	 */
	private Set<String> getTemplateQuestionNames(Long templateId) {
		SurveyTemplate template = surveyTemplateRepository.findOne(templateId);
		SurveyJs surveyJsModel = SurveyUtils.parseSurveyModel(template.getJsSurvey());
		return SurveyUtils.getQuestionNamesFromSurveyModel(surveyJsModel);
	}

	/**
	 *
	 * @param questionNames
	 * @param surveyAnswers
	 * @return
	 */
	private static String transformSurveyAnswersToCSV(Set<String> questionNames, Map<String, Object> surveyAnswers,
			boolean withHeaders) {
		Map<String, Object> orderedAnswers = new LinkedHashMap<>();
		questionNames.stream().forEach(name -> orderedAnswers.put(name, surveyAnswers.get(name)));
		return SurveyUtils.formatAnswersAsCSV(orderedAnswers, withHeaders);
	}
}

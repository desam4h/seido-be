package co.com.m4h.registros.common.utils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import co.com.m4h.registros.json.SurveyJs;

public class SurveyUtils {
	/**
	 * Parse the survey answers given in a SurveyJS survey to a Java Map.
	 * 
	 * @param jsonAnswers
	 *            Source with the survey answers in json format.
	 * @return A map object with key=questionName & value=questionAnswer.
	 * @throws IllegalArgumentException
	 *             If survey answers doesn't represent a valid json
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseSurveyAnswers(String jsonAnswers) throws IllegalArgumentException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(jsonAnswers, LinkedHashMap.class);
		} catch (IOException e) {
			throw new IllegalArgumentException("Error trying to parse the surveyAnswers", e);
		}
	}

	/**
	 * Parse the survey model to a SurveyJS object.
	 * 
	 * @param surveyModel
	 *            Source with the survey model in json format.
	 * @return Object SurveyJS with the survey model.
	 * @throws IllegalArgumentException
	 *             If survey model doesn't represent a valid json model.
	 */
	public static SurveyJs parseSurveyModel(String surveyModel) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(surveyModel, SurveyJs.class);
		} catch (IOException e) {
			throw new IllegalArgumentException("Error trying to parse the surveyModel", e);
		}
	}

	/**
	 * Determine if given a survey model and the set of answers, all answers are
	 * related with the survey.
	 * 
	 * @param surveyModel
	 *            Object with the survey information
	 * @param surveyAnswers
	 *            Map with the survey answers
	 * @return True if all answers are part of the survey (answer key is a question
	 *         in the survey)s
	 */
	public static boolean validateSurveyAnswers(String surveyModel, String surveyAnswers) {
		SurveyJs surveyJs = parseSurveyModel(surveyModel);
		Map<String, Object> answers = parseSurveyAnswers(surveyAnswers);
		return validateSurveyAnswers(surveyJs, answers);
	}

	/**
	 * Determine if given a survey model and the set of answers, all answers are
	 * related with the survey.
	 * 
	 * @param model
	 *            Object with the survey information
	 * @param answers
	 *            Map with the survey answers
	 * @return True if all answers are part of the survey (answer key is a question
	 *         in the survey)s
	 */
	private static boolean validateSurveyAnswers(SurveyJs model, Map<String, Object> answers) {
		// Get all question names from the survey model
		Set<String> questionNames = getQuestionNamesFromSurveyModel(model);
		// Filter answers that are not present in the model
		long invalidAnswers = answers.keySet().stream().filter(answerKey -> !questionNames.contains(answerKey)).count();

		return invalidAnswers == 0;
	}

	/**
	 * Transform the answers given in a survey to a csv format
	 * 
	 * @param answers
	 *            Information to transform to csv.
	 * @param withHeaders
	 *            Determine if names of questions are required as csv headers.
	 * @return String with the answers in the csv format.
	 */
	public static String formatAnswersAsCSV(Map<String, Object> answers, boolean withHeaders) {
		try {
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = CsvSchema.builder().addColumns(answers.keySet(), CsvSchema.ColumnType.STRING)
					.setUseHeader(withHeaders).build();
			return mapper.writer(schema).writeValueAsString(answers);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Error trying to format survey answers to CSV", e);
		}
	}

	/**
	 * Determine if the survey has finished.
	 * 
	 * @param model
	 *            Survey model owner of the questions.
	 * @param answers
	 *            Survey answers with the values selected in each question.
	 * @return True if the survey model has the same questions than the survey
	 *         answers
	 */
	public static boolean isSurveyFinished(SurveyJs model, Map<String, Object> answers) {
		// Get all question names from the survey model
		Set<String> questionNames = getQuestionNamesFromSurveyModel(model);
		long questionsWithoutAnswer = questionNames.stream().filter(question -> !answers.containsKey(question)).count();
		return questionNames.size() == answers.size() && questionsWithoutAnswer == 0;
	}

	/**
	 * Utility method that analise the survey model and extract the question names
	 * 
	 * @param model
	 *            Object with the information to analise
	 * @return Set of question names
	 */
	private static Set<String> getQuestionNamesFromSurveyModel(SurveyJs model) {
		return model.getPages().stream().flatMap(page -> page.elements.stream()).map(question -> question.name)
				.collect(Collectors.toSet());
	}
}

package co.com.m4h.seido.common;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import co.com.m4h.seido.json.SurveyJs;

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

	public static String transformQuestionsAndAnswersToJson(Set<String> questions, List<String> responseColumns) {
		if (questions.size() != responseColumns.size()) {
			throw new IllegalArgumentException("::: Invalid answers for given model");
		}

		Map<String, String> answers = new LinkedHashMap<>();
		Iterator<String> answerIterator = responseColumns.iterator();

		questions.forEach(question -> {
			answers.put(question, answerIterator.next());
		});

		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(answers);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("::: Error trying to transform to json from map", e);
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

	public static MappingIterator<String[]> readAnswersFromCSV(Set<String> questions, String answers)
			throws IOException {
		CsvSchema schema = CsvSchema.builder().setSkipFirstDataRow(true).build();
		CsvMapper mapper = new CsvMapper();
		mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
		return mapper.readerFor(String[].class).with(schema).readValues(answers);
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
		return questionsWithoutAnswer == 0; // questionNames.size() == answers.size() &&
	}

	/**
	 * Utility method that analyze the survey model and extract the question names
	 * 
	 * @param model
	 *            Object with the information to analyze
	 * @return Set of question names
	 */
	public static Set<String> getQuestionNamesFromSurveyModel(SurveyJs model) {
		return model.getPages().stream().flatMap(page -> page.elements.stream()).map(question -> question.name)
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	/**
	 * Utility method that analyze the survey model and extract the question names
	 * and titles
	 * 
	 * @param model
	 *            Object with the information to analyze
	 * @return Set of question names
	 */
	public static Map<String, String> getQuestionsFromSurveyModel(SurveyJs model) {
		Map<String, String> res = new LinkedHashMap<>();

		model.getPages().stream().flatMap(page -> page.elements.stream())
				.forEach(question -> res.put(question.name, question.title));

		return res;
	}
}

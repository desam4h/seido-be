package co.com.m4h.seido.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Hex;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

	@Override
	@Transactional(readOnly = true)
	public File getExcel(Long templateId) {
		try {

			SurveyTemplate template = surveyTemplateRepository.findOne(templateId);
			Set<String> questionNames = getTemplateQuestionNames(templateId);

			////////////////////
			Stream<Survey> surveyStream = surveyRepository.findAllByTemplateId(templateId);

			Stream<Map<String, Object>> answersStream = surveyStream
					.filter(s -> !s.getState().equals(SurveyState.NOT_STARTED)).map(Survey::getSurveyAnswers)
					.map(SurveyUtils::parseSurveyAnswers);

			List<Map<String, Object>> answers = answersStream.collect(Collectors.toList());

			System.out.println("::: " + answers.size());

			////////////////////

			int maxCol = questionNames.size() - 1;

			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("Estadisticas");

			Footer footer = sheet.getFooter();
			footer.setRight("Pág. " + HeaderFooter.page() + " de " + HeaderFooter.numPages());

			sheet.getPrintSetup().setPaperSize(XSSFPrintSetup.LETTER_PAPERSIZE);
			sheet.getPrintSetup().setLandscape(false);
			sheet.getPrintSetup().setFitWidth((short) 1);
			// sheet.setFitToPage( true );
			sheet.setAutobreaks(true);

			XSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 11);
			font.setFontName("Arial");
			font.setColor(IndexedColors.AUTOMATIC.getIndex());
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			font.setItalic(false);

			XSSFCellStyle boldStyle = wb.createCellStyle();
			boldStyle.setFont(font);
			boldStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);

			for (int i = 0; i < 5; i++)
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, maxCol));

			int rowActual = 0;
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			XSSFCell celda = null;

			// Títulos de Archivo
			celda = sheet.createRow(rowActual++).createCell(0);
			celda.setCellValue("SEIDO");
			celda.setCellStyle(boldStyle);

			celda = sheet.createRow(rowActual++).createCell(0);
			celda.setCellValue(template.getSpecialty().getCompany().getName().toUpperCase());
			celda.setCellStyle(boldStyle);

			celda = sheet.createRow(rowActual++).createCell(0);
			celda.setCellValue(template.getSpecialty().getName().toUpperCase());
			celda.setCellStyle(boldStyle);

			celda = sheet.createRow(rowActual++).createCell(0);
			celda.setCellValue(template.getName().toUpperCase());
			celda.setCellStyle(boldStyle);

			celda = sheet.createRow(rowActual++).createCell(0);
			celda.setCellValue("GENERADO EL " + sdf.format(new Date(System.currentTimeMillis())));
			celda.setCellStyle(boldStyle);

			// Repite la fila de títulos y una fila vacía debajo en todas las hojas al
			// imprimir
			int filaTitulos = ++rowActual;
			String filasRepetir = String.valueOf(1) + ":" + String.valueOf(filaTitulos + 2);
			sheet.setRepeatingRows(CellRangeAddress.valueOf(filasRepetir));

			// Títulos de Columnas
			int i = 0;
			for (String question : questionNames) {
				if (i == 0)
					celda = sheet.createRow(rowActual).createCell(i++);
				else
					celda = sheet.getRow(rowActual).createCell(i++);

				celda.setCellValue("  " + question + "  ");
				celda.setCellStyle(boldStyle);
			}

			rowActual += 2;

			Iterator<Map<String, Object>> iterator = answers.iterator();

			while (iterator.hasNext()) {
				Map<String, Object> answer = iterator.next();
				XSSFRow row = sheet.createRow(rowActual++);

				i = 0;
				for (String question : questionNames) {
					String str = answer.get(question) == null ? "" : answer.get(question).toString();
					row.createCell(i++).setCellValue(str);
				}
			}

			for (i = 0; i <= maxCol; i++)
				sheet.autoSizeColumn(i);

			// ////////////////// Protección del archivo para edición ////////////////////

			String password = getCadenaHexaAleatoria(4);
			byte[] pwdBytes = null;

			pwdBytes = Hex.decodeHex(password.toCharArray());

			sheet.lockDeleteColumns();
			sheet.lockDeleteRows();
			sheet.lockFormatCells();
			// sheet.lockFormatColumns();
			// sheet.lockFormatRows();
			sheet.lockInsertColumns();
			sheet.lockInsertRows();
			sheet.getCTWorksheet().getSheetProtection().setPassword(pwdBytes);

			sheet.enableLocking();
			wb.lockStructure();

			// //////////////////////////////////////////////////////////////////////////

			File file = new File("file" + System.currentTimeMillis() + ".xlsx");
			FileOutputStream fichero = new FileOutputStream(file);
			wb.write(fichero);

			fichero.close();

			return file;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return null;

		// // Se crea el libro
		// HSSFWorkbook libro = new HSSFWorkbook();
		//
		// // Se crea una hoja dentro del libro
		// HSSFSheet hoja = libro.createSheet();
		//
		// // Se crea una fila dentro de la hoja
		// HSSFRow fila = hoja.createRow(0);
		//
		// // Se crea una celda dentro de la fila
		// HSSFCell celda = fila.createCell((short) 0);
		//
		// // Se crea el contenido de la celda y se mete en ella.
		// HSSFRichTextString texto = new HSSFRichTextString("hola mundo prueba");
		// celda.setCellValue(texto);
		//
		// // Se salva el libro.
		// try {
		// File file = new File("holamundo.xls");
		// FileOutputStream elFichero = new FileOutputStream(file);
		// libro.write(elFichero);
		//
		// libro.close();
		// elFichero.close();
		//
		// return file;
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// return null;
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

	private static String getCadenaHexaAleatoria(int longitud) {
		String cadenaAleatoria = "";
		long milis = new java.util.GregorianCalendar().getTimeInMillis();
		Random r = new Random(milis);
		int i = 0;

		while (i < longitud) {
			char c = (char) r.nextInt(255);
			if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F')) {
				cadenaAleatoria += c;
				i++;
			}
		}

		return cadenaAleatoria;
	}
}

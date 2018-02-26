package co.com.m4h.seido.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
import co.com.m4h.seido.json.Control6Meses;
import co.com.m4h.seido.model.Patient;
import co.com.m4h.seido.model.Survey;
import co.com.m4h.seido.persistence.SurveyRepository;
import co.com.m4h.seido.service.AddonsService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Jose Molina on 12/2/18.
 */
@Service
@Slf4j
public class AddonsServiceImpl implements AddonsService {

	@Autowired
	private SurveyRepository surveyRepository;

	private static final Long TEMPLATE_CX_ID = 94L;

	private static final Long TEMPLATE_CONTROL_6_ID = 96L;

	private static final String QUESTION_NAME_CX_DATE = "surgeryDate";

	private static final Long TEMPLATE_GENERAL_ID = 38L;

	private static final String QUESTION_NAME_EMAIL = "Correo electrónico";

	private static final double AVERAGE_MILLIS_PER_MONTH = 365.24 * 24 * 60 * 60 * 1000 / 12;

	@Override
	@Transactional(readOnly = true)
	public List<Control6Meses> findPatientsToControl() {

		List<Control6Meses> res = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date(System.currentTimeMillis());

		try {

			// Stream<Survey> surveyStream =
			// surveyRepository.findByTemplateIdStartedAndFinished(TEMPLATE_CX_ID);
			// List<Survey> surveys = surveyStream.collect(Collectors.toList());

			List<Survey> surveys = surveyRepository.findSurveysToControl6(TEMPLATE_CX_ID, TEMPLATE_CONTROL_6_ID);

			for (Survey survey : surveys) {

				String surgeryDateStr = (String) SurveyUtils.parseSurveyAnswers(survey.getSurveyAnswers())
						.get(QUESTION_NAME_CX_DATE);

				if (surgeryDateStr != null && !surgeryDateStr.equals("")) {
					Date surgeryDate = sdf.parse(surgeryDateStr);

					if (monthsBetween(surgeryDate, now) > 5) {
						Patient p = survey.getPatient();

						Survey generalSurvey = surveyRepository.findByPatientIdAndTemplateId(p.getId(),
								TEMPLATE_GENERAL_ID);
						String email = (String) SurveyUtils.parseSurveyAnswers(generalSurvey.getSurveyAnswers())
								.get(QUESTION_NAME_EMAIL);
						if (email == null)
							email = "";

						res.add(new Control6Meses(p.getId(),
								p.getFirstName() == null ? ""
										: p.getFirstName() + " " + p.getLastName() == null ? "" : p.getLastName(),
								surgeryDateStr, email));
					}
				}

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	private static double monthsBetween(Date d1, Date d2) {
		return (d2.getTime() - d1.getTime()) / AVERAGE_MILLIS_PER_MONTH;
	}

	@Override
	@Transactional(readOnly = true)
	public File getExcel() {
		try {
			String[] titles = { "ID DE PACIENTE", "NOMBRE DE PACIENTE", "FECHA DE CIRUGÍA", "CORREO ELECTRÓNICO" };
			List<Control6Meses> data = findPatientsToControl();

			int maxCol = 3;

			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("Control 6 meses");

			Footer footer = sheet.getFooter();
			footer.setRight("Pág. " + HeaderFooter.page() + " de " + HeaderFooter.numPages());

			sheet.getPrintSetup().setPaperSize(XSSFPrintSetup.LETTER_PAPERSIZE);
			sheet.getPrintSetup().setLandscape(true);
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

			for (int i = 0; i < 4; i++)
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, maxCol));

			int rowActual = 0;
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			XSSFCell celda = null;

			// Títulos de Archivo
			celda = sheet.createRow(rowActual++).createCell(0);
			celda.setCellValue("ORTOSANTAFE SAS");
			celda.setCellStyle(boldStyle);

			celda = sheet.createRow(rowActual++).createCell(0);
			celda.setCellValue("ORTOPEDIA - RODILLA");
			celda.setCellStyle(boldStyle);

			celda = sheet.createRow(rowActual++).createCell(0);
			celda.setCellValue("LISTADO DE PACIENTES - CONTROL 6 MESES ");
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
			for (String title : titles) {
				if (i == 0)
					celda = sheet.createRow(rowActual).createCell(i++);
				else
					celda = sheet.getRow(rowActual).createCell(i++);

				celda.setCellValue("  " + title + "  ");
				celda.setCellStyle(boldStyle);
			}

			rowActual++;

			for (Control6Meses c : data) {
				XSSFRow row = sheet.createRow(rowActual++);

				row.createCell(0).setCellValue(c.getPatientId());
				row.createCell(1).setCellValue(c.getName());
				row.createCell(2).setCellValue(c.getSurgeryDate());
				row.createCell(3).setCellValue(c.getEmail());
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

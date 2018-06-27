package co.com.m4h.seido.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.com.m4h.seido.common.Constant;
import co.com.m4h.seido.model.SurveyTemplate;
import co.com.m4h.seido.security.service.JwtMessageResponse;
import co.com.m4h.seido.service.SurveyService;
import co.com.m4h.seido.service.SurveyTemplateService;
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
	private static final String TEMPLATE_ID = "templateId";

	@Autowired
	private SurveyTemplateService surveyTemplateService;

	@Autowired
	private SurveyService surveyService;

	// @Autowired
	// private UploaderService uploaderService;

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

	@RequestMapping(value = "/{templateId}/statistics", method = RequestMethod.GET)
	public ResponseEntity<JwtMessageResponse> buildStatisticsByTemplate(@PathVariable(TEMPLATE_ID) Long templateId) {
		String statisticsAsCsv = surveyService.getStatistics(templateId);
		JwtMessageResponse res = new JwtMessageResponse(statisticsAsCsv);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@RequestMapping(value = "/{templateId}/excel", method = RequestMethod.GET)
	public void buildExcel(@PathVariable(TEMPLATE_ID) Long templateId, HttpServletResponse response) {

		try {
			File file = surveyService.getExcel(templateId);

			response.addHeader("Content-Disposition", "attachment; filename=" + file.getName());
			response.setContentType(Files.probeContentType(file.toPath()));
			response.setContentLengthLong(file.length());

			FileInputStream fis = new FileInputStream(file);
			int c;
			while ((c = fis.read()) > -1) {
				response.getOutputStream().write(c);
			}

			response.flushBuffer();

			fis.close();
			file.delete();
		} catch (IOException e) {
			System.out.println("::::: error: " + e.getMessage());
		}

	}

	@RequestMapping(value = "/excelGeneral", method = RequestMethod.GET)
	public void buildExcelGeneral(@PathVariable(SPECIALTY_ID_PARAM) Long specialtyId, HttpServletResponse response) {

		try {
			File file = surveyService.getExcelGeneral(specialtyId);

			response.addHeader("Content-Disposition", "attachment; filename=" + file.getName());
			response.setContentType(Files.probeContentType(file.toPath()));
			response.setContentLengthLong(file.length());

			FileInputStream fis = new FileInputStream(file);
			int c;
			while ((c = fis.read()) > -1) {
				response.getOutputStream().write(c);
			}

			response.flushBuffer();

			fis.close();
			file.delete();
		} catch (IOException e) {
			System.out.println("::::: error: " + e.getMessage());
		}

	}

	// @RequestMapping(value = "/{templateId}/upload", method = RequestMethod.POST)
	// public ResponseEntity<String> uploadInfoByTemplate(@PathVariable(TEMPLATE_ID)
	// Long templateId,
	// @RequestBody String csvInfo) {
	// System.out.println("::: csvInfo = " + csvInfo);
	// int itemsUploaded = uploaderService.uploadInfo(templateId, csvInfo);
	// String response = "{\"uploadedRows\":%s}";
	// return new ResponseEntity<>(String.format(response,
	// Integer.toString(itemsUploaded)), HttpStatus.OK);
	// }
}
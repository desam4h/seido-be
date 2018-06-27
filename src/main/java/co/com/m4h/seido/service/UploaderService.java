package co.com.m4h.seido.service;

public interface UploaderService {
	/**
	 * Load surveys information massively.
	 * 
	 * @param templateId
	 *            {@link co.zero.health.model.SurveyTemplate}
	 * @param info
	 *            Information in csv format with header. All columns should match
	 *            the order of template
	 * @return Amount of surveys generated
	 */
	// int uploadInfo(Long templateId, String info);
}

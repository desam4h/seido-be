package co.com.m4h.seido.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.com.m4h.seido.common.SurveyUtils;
import co.com.m4h.seido.json.Control6Meses;
import co.com.m4h.seido.model.Patient;
import co.com.m4h.seido.model.Survey;
import co.com.m4h.seido.persistence.SurveyRepository;
import co.com.m4h.seido.service.AddonsService;

/**
 * Created by Jose Molina on 12/2/18.
 */
@Service
public class AddonsServiceImpl implements AddonsService {

	@Autowired
	private SurveyRepository surveyRepository;

	private static final Long TEMPLATE_CX_ID = 94L;

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

			Stream<Survey> surveyStream = surveyRepository.findByTemplateIdStartedAndFinished(TEMPLATE_CX_ID);
			List<Survey> surveys = surveyStream.collect(Collectors.toList());

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

						res.add(new Control6Meses(p.getId(), p.getFirstName() + p.getLastName(), surgeryDateStr,
								email));
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
}

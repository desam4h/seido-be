package co.com.m4h.seido.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.m4h.seido.model.Specialty;
import co.com.m4h.seido.model.SurveyTemplate;
import co.com.m4h.seido.persistence.SpecialtyRepository;
import co.com.m4h.seido.persistence.SurveyTemplateRepository;
import co.com.m4h.seido.service.SurveyTemplateService;

/**
 * Created by hernan on 7/2/17.
 */
@Service
public class SurveyTemplateServiceImpl implements SurveyTemplateService {

	@Autowired
	private SpecialtyRepository specialtyRepository;

	@Autowired
	private SurveyTemplateRepository surveyTemplateRepository;

	@Override
	public List<SurveyTemplate> findAllBySpecialtyId(Long specialtyId) {
		return surveyTemplateRepository.findAllBySpecialtyId(specialtyId);
	}

	@Override
	public SurveyTemplate save(SurveyTemplate surveyTemplate, Long specialtyId) {

		SurveyTemplate saved = null;
		Specialty specialty = specialtyRepository.findOne(specialtyId);
		// Long companyId = SecurityUtil.getCompanyId();

		// if (!specialty.getCompany().getId().equals(companyId)) {
		// throw new IllegalArgumentException("Forbidden company to add specialty");
		// } else {
		surveyTemplate.setSpecialties(Arrays.asList(specialty));

		List<SurveyTemplate> templates = surveyTemplateRepository.findAllBySpecialtyId(specialtyId);

		if (templates != null && templates.size() > 0 && surveyTemplate.getId() != null) {
			int index = 0;

			for (int i = 0; i < templates.size(); i++) {
				SurveyTemplate t = templates.get(i);

				if (t.getId().equals(surveyTemplate.getId())) {
					index = i;
					surveyTemplate.setSpecialties(t.getSpecialties());
				}
			}

			templates.remove(index);
		}

		if (templates == null || templates.size() == 0) {
			surveyTemplate.setOrder_id(1);

			saved = surveyTemplateRepository.save(surveyTemplate);
		} else {
			int userOrder = surveyTemplate.getOrder_id();
			int nextOrder = surveyTemplate.getOrder_id();

			for (int i = 1; i <= templates.size(); i++) {

				if (i >= userOrder) {

					if (saved == null && i == userOrder) {
						surveyTemplate.setOrder_id(nextOrder);

						saved = surveyTemplateRepository.save(surveyTemplate);

						nextOrder++;
					}

					SurveyTemplate t = templates.get(i - 1);
					t.setOrder_id(nextOrder);

					surveyTemplateRepository.save(t);

					nextOrder++;
				}
			}

			if (saved == null) {
				surveyTemplate.setOrder_id(templates.size() + 1);

				saved = surveyTemplateRepository.save(surveyTemplate);
			}
		}

		return saved;
		// }
	}

	@Override
	public SurveyTemplate update(SurveyTemplate surveyTemplate, Long specialtyId) {

		// TODO: When model is modified the surveys could change to NOT SOLVED
		// Maybe that could be handled with a PATCH ???

		return save(surveyTemplate, specialtyId);
	}

	@Override
	public Optional<SurveyTemplate> find(Long surveyTemplateId) {
		return Optional.ofNullable(surveyTemplateRepository.findOne(surveyTemplateId));
	}

	@Override
	public void delete(Long surveyTemplateId) {
		surveyTemplateRepository.delete(surveyTemplateId);
	}
}

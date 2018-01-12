package co.com.m4h.seido.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.com.m4h.seido.model.SurveyTemplate;
import co.com.m4h.seido.model.SurveyType;

@Repository
public interface SurveyTemplateRepository extends PagingAndSortingRepository<SurveyTemplate, Long> {
	List<SurveyTemplate> findAllBySpecialtyId(Long specialtyId);

	@Query("SELECT t FROM SurveyTemplate t WHERE t.specialty.company.id = :companyId AND t.type = :type")
	List<SurveyTemplate> findAllByCompanyId(@Param("companyId") Long companyId, @Param("type") SurveyType type);
}
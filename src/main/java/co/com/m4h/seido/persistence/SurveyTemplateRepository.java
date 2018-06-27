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

	// @Query("SELECT t FROM SurveyTemplate t WHERE t.specialty.id = :specialtyId
	// order by t.order_id asc")
	@Query("SELECT DISTINCT t FROM SurveyTemplate t JOIN t.specialties s WHERE s.id = :specialtyId order by t.order_id asc")
	List<SurveyTemplate> findAllBySpecialtyId(@Param("specialtyId") Long specialtyId);

	// @Query("SELECT t FROM SurveyTemplate t WHERE t.specialty.company.id =
	// :companyId AND t.type = :type order by t.order_id asc")
	@Query("SELECT DISTINCT t FROM SurveyTemplate t JOIN t.specialties s WHERE s.company.id = :companyId AND t.type = :type order by t.order_id asc")
	List<SurveyTemplate> findAllByCompanyId(@Param("companyId") Long companyId, @Param("type") SurveyType type);
}
package co.com.m4h.seido.persistence;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

import java.util.List;
import java.util.stream.Stream;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.com.m4h.seido.model.Survey;

@Repository
public interface SurveyRepository extends PagingAndSortingRepository<Survey, Long> {

	public static final String QUERY_CONTROL_6_MESES = "SELECT DISTINCT s1 FROM Survey s1, Survey s2 "
			+ "WHERE s1.template.id = :templateCirugiaId AND s1.surveyAnswers IS NOT NULL AND s2.template.id = :templateControl6Id "
			+ "AND s2.event.id = s1.event.id AND s2.state = 'NOT_STARTED'";

	@Query("SELECT s FROM Survey s WHERE s.patient.id = :patientId ORDER BY s.id ASC")
	List<Survey> findAllByPatientId(@Param("patientId") Long patientId);

	void deleteAllByEventId(Long eventId);

	void deleteAllByPatientId(Long patientId);

	/**
	 * List all surveys filtering by Template
	 * 
	 * @param templateId
	 *            Identifier of the template filter
	 * @return Stream of all surveys related with the given template
	 */
	@QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "1"))
	Stream<Survey> findAllByTemplateId(Long templateId);

	Survey findByEventIdAndTemplateId(Long eventId, Long templateId);

	Survey findByPatientIdAndTemplateId(Long patientId, Long templateId);

	@Query("SELECT s FROM Survey s WHERE s.template.id = :templateId AND (s.state = 'STARTED' OR s.state = 'FINISHED')")
	Stream<Survey> findByTemplateIdStartedAndFinished(@Param("templateId") Long templateId);

	@Query(QUERY_CONTROL_6_MESES)
	List<Survey> findSurveysToControl6(@Param("templateCirugiaId") Long templateCirugiaId,
			@Param("templateControl6Id") Long templateControl6Id);
}
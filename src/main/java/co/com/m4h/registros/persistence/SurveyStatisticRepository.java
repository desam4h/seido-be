package co.com.m4h.registros.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import co.com.m4h.registros.model.SurveyStatistics;

@Repository
public interface SurveyStatisticRepository extends PagingAndSortingRepository<SurveyStatistics, Long> {
	void deleteAllByEventId(Long eventId);

	void deleteAllByPatientId(Long patientId);
}
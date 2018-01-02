package co.com.m4h.registros.persistence;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import co.com.m4h.registros.model.Survey;

@Repository
public interface SurveyRepository extends PagingAndSortingRepository<Survey, Long> {
	List<Survey> findAllByPatientId(Long patientId);

	void deleteAllByEventId(Long eventId);

	void deleteAllByPatientId(Long patientId);
}
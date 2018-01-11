package co.com.m4h.registros.persistence;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import co.com.m4h.registros.model.Event;

@Repository
public interface EventRepository extends PagingAndSortingRepository<Event, Long> {
	List<Event> findAllByPatientId(Long patientId);

	void deleteAllByPatientId(Long patientId);
}

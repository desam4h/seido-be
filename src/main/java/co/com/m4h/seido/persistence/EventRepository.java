package co.com.m4h.seido.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.com.m4h.seido.model.Event;

@Repository
public interface EventRepository extends PagingAndSortingRepository<Event, Long> {

	List<Event> findAllByOrderByPatientIdAsc(Long patientId);

	void deleteAllByPatientId(Long patientId);

	Event findOneByPatientIdAndLoadedId(Long patientId, String loadedId);

	@Query("SELECT e FROM Event e WHERE e.specialty.id = :specialtyId AND e.patient.id = :PatientId order by e.createdDate asc")
	List<Event> findEventsBySpecialtyIdAndPatientId(@Param("specialtyId") Long specialtyId,
			@Param("PatientId") Long PatientId);
}

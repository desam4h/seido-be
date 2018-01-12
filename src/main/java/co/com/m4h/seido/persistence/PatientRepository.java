package co.com.m4h.seido.persistence;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import co.com.m4h.seido.model.Patient;

@Repository
public interface PatientRepository extends PagingAndSortingRepository<Patient, Long> {
	List<Patient> findAllByCompanyId(Long companyId);

	/**
	 * Find a {@link Patient} by his/her national identification
	 * 
	 * @param companyId
	 *            company owner of the patient (Multiple companies can have the same
	 *            patient).
	 * @param nuip
	 *            Patient identification
	 * @return Patient if exist
	 */
	Patient findOneByCompanyIdAndNuip(Long companyId, String nuip);

	Patient findOneByNuip(String nuip);
}
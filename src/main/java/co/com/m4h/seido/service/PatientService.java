package co.com.m4h.seido.service;

import java.util.List;
import java.util.Optional;

import co.com.m4h.seido.model.Patient;

/**
 * Created by hernan on 7/2/17.
 */
public interface PatientService extends GenericCrud<Patient> {
	List<Patient> findAll();

	/**
	 * Find a {@link Patient} by his/her national identification
	 * 
	 * @param nuip
	 *            Patient identification
	 * @return Patient if exist
	 */
	Optional<Patient> findByNuip(String nuip);
}
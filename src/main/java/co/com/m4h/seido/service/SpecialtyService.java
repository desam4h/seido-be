package co.com.m4h.seido.service;

import java.util.List;

import co.com.m4h.seido.model.Specialty;

/**
 * Created by hernan on 7/2/17.
 */
public interface SpecialtyService extends GenericCrud<Specialty> {
	List<Specialty> findAllByCompanyId(Long companyId);

	List<Specialty> findAll();
}
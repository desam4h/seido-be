package co.com.m4h.registros.service;

import java.util.List;

import co.com.m4h.registros.model.Company;

/**
 * Created by hernan on 7/2/17.
 */
public interface CompanyService extends GenericCrud<Company> {
	List<Company> findAll();
}
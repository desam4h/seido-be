package co.com.m4h.seido.service;

import java.util.List;

import co.com.m4h.seido.model.Company;

/**
 * Created by hernan on 7/2/17.
 */
public interface CompanyService extends GenericCrud<Company> {
	List<Company> findAll();
}
package co.com.m4h.registros.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import co.com.m4h.registros.model.Company;
import co.com.m4h.registros.persistence.CompanyRepository;
import co.com.m4h.registros.service.CompanyService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by hernan on 7/4/17.
 */
@Service
@Slf4j
public class CompanyServiceImpl implements CompanyService {
	@Autowired
	private CompanyRepository companyRepository;

	@Override
	public Company save(Company company) {
		return companyRepository.save(company);
	}

	@Override
	public void delete(Long companyId) {
		try {
			companyRepository.delete(companyId);
		} catch (EmptyResultDataAccessException e) {
			log.warn(e.getMessage());
		}
	}

	@Override
	public Optional<Company> find(Long companyId) {
		return Optional.ofNullable(companyRepository.findOne(companyId));
	}

	@Override
	public List<Company> findAll() {
		return companyRepository.findAll();
	}
}
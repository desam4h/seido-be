package co.com.m4h.seido.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.m4h.seido.model.Company;
import co.com.m4h.seido.persistence.CompanyRepository;
import co.com.m4h.seido.service.CompanyService;

/**
 * Created by hernan on 7/4/17.
 */
@Service
public class CompanyServiceImpl implements CompanyService {
	@Autowired
	private CompanyRepository companyRepository;

	@Override
	public Company save(Company company) {
		company.setCreatedDate(LocalDate.now());
		return companyRepository.save(company);
	}

	@Override
	public void delete(Long companyId) {
		companyRepository.delete(companyId);
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

package co.com.m4h.registros.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.m4h.registros.common.SecurityUtil;
import co.com.m4h.registros.model.Specialty;
import co.com.m4h.registros.persistence.SpecialtyRepository;
import co.com.m4h.registros.service.SpecialtyService;

/**
 * Created by hernan on 7/2/17.
 */
@Service
public class SpecialtyServiceImpl implements SpecialtyService {

	@Autowired
	private SpecialtyRepository specialtyRepository;

	@Override
	public Specialty save(Specialty specialty) {
		specialty.setCompany(SecurityUtil.getCompany());
		return specialtyRepository.save(specialty);
	}

	@Override
	public void delete(Long specialtyId) {
		specialtyRepository.delete(specialtyId);
	}

	@Override
	public Optional<Specialty> find(Long specialtyId) {
		return Optional.ofNullable(specialtyRepository.findOne(specialtyId));
	}

	@Override
	public List<Specialty> findAll() {
		return specialtyRepository.findAll();
	}

	@Override
	public List<Specialty> findAllByCompanyId(Long companyId) {
		return specialtyRepository.findAllByCompanyId(companyId);
	}
}

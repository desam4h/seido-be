package co.com.m4h.registros.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import co.com.m4h.registros.model.Specialty;
import co.com.m4h.registros.persistence.SpecialtyRepository;
import co.com.m4h.registros.service.SpecialtyService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by hernan on 7/2/17.
 */
@Service
@Slf4j
public class SpecialtyServiceImpl implements SpecialtyService {
	@Autowired
	private SpecialtyRepository specialtyRepository;

	@Override
	public Specialty save(Specialty specialty) {
		// TODO: specialty.setcompany se debe hacer desde ui
		return specialtyRepository.save(specialty);
	}

	@Override
	public void delete(Long specialtyId) {
		try {
			specialtyRepository.delete(specialtyId);
		} catch (EmptyResultDataAccessException e) {
			log.warn(e.getMessage());
		}
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

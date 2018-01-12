package co.com.m4h.seido.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.com.m4h.seido.common.Constant;
import co.com.m4h.seido.common.SecurityUtil;
import co.com.m4h.seido.model.Specialty;
import co.com.m4h.seido.service.SpecialtyService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by hernan on 7/2/17.
 */
@Slf4j
@RestController
@RequestMapping(value = "/specialty", produces = Constant.CONTENT_TYPE_JSON)
public class SpecialtyController {

	// consumes = Constant.CONTENT_TYPE_JSON,

	@Autowired
	private SpecialtyService specialtyService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Specialty>> findAll() {

		if (SecurityUtil.getRole().equals("ROLE_ROOT")) {
			List<Specialty> specialties = specialtyService.findAll();

			return new ResponseEntity<>(specialties, HttpStatus.OK);
		} else {
			Long companyId = SecurityUtil.getCompanyId();
			List<Specialty> specialties = specialtyService.findAllByCompanyId(companyId);

			return new ResponseEntity<>(specialties, HttpStatus.OK);
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Specialty> save(@RequestBody Specialty specialty) {
		Specialty persistedSpecialty = specialtyService.save(specialty);
		return new ResponseEntity<>(persistedSpecialty, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{specialtyId}", method = RequestMethod.GET)
	public ResponseEntity<Specialty> find(@PathVariable("specialtyId") Long specialtyId) {
		return specialtyService.find(specialtyId).map(specialty -> new ResponseEntity<>(specialty, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<Specialty> update(@RequestBody Specialty specialty) {
		if (specialty.getId() != null) {
			Specialty persistedSpecialty = specialtyService.update(specialty);
			return new ResponseEntity<>(persistedSpecialty, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/{specialtyId}", method = RequestMethod.DELETE)
	public ResponseEntity<Specialty> delete(@PathVariable("specialtyId") Long specialtyId) {
		try {
			specialtyService.delete(specialtyId);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);

		} catch (DataIntegrityViolationException e) {
			log.warn(e.getMessage());
			return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);

		} catch (Exception e) {
			log.warn(e.getMessage());
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
}
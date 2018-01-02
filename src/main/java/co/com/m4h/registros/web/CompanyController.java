package co.com.m4h.registros.web;

import java.time.LocalDate;
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

import co.com.m4h.registros.common.Constant;
import co.com.m4h.registros.model.Company;
import co.com.m4h.registros.service.CompanyService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by hernan on 7/4/17.
 */
@Slf4j
@RestController
@RequestMapping(value = "/company", produces = Constant.CONTENT_TYPE_JSON)
public class CompanyController {
	// consumes = Constant.CONTENT_TYPE_JSON,
	// private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private CompanyService companyService;

	@RequestMapping(method = RequestMethod.GET)
	public List<Company> listAll() {
		List<Company> companies = companyService.findAll();
		return companies;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Company> save(@RequestBody Company company) {
		company.setCreatedDate(LocalDate.now());
		Company persistedCompany = companyService.save(company);
		return new ResponseEntity<>(persistedCompany, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{companyId}", method = RequestMethod.GET)
	public ResponseEntity<Company> find(@PathVariable("companyId") Long companyId) {
		return companyService.find(companyId).map(company -> new ResponseEntity<>(company, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<Company> update(@RequestBody Company company) {
		if (company.getId() != null) {
			Company persistedCompany = companyService.update(company);
			return new ResponseEntity<>(persistedCompany, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/{companyId}", method = RequestMethod.DELETE)
	public ResponseEntity<Company> delete(@PathVariable("companyId") Long companyId) {
		try {
			companyService.delete(companyId);
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

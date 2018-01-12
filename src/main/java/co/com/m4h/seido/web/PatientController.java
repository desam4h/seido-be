package co.com.m4h.seido.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.com.m4h.seido.common.Constant;
import co.com.m4h.seido.model.Patient;
import co.com.m4h.seido.service.PatientService;

/**
 * Created by hernan on 7/2/17.
 */
@RestController
@RequestMapping(value = "/patient", produces = Constant.CONTENT_TYPE_JSON)
public class PatientController {
	// consumes = Constant.CONTENT_TYPE_JSON,

	private static final String PATIENT_ID_PARAM = "patientId";

	private static final String NUIP_PARAM = "nuip";

	@Autowired
	private PatientService patientService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Patient>> findAll() {
		List<Patient> patients = patientService.findAll();
		return new ResponseEntity<>(patients, HttpStatus.OK);
	}

	@RequestMapping(value = "/{patientId}", method = RequestMethod.GET)
	public ResponseEntity<Patient> find(@PathVariable(PATIENT_ID_PARAM) Long patientId) {
		return patientService.find(patientId).map(patient -> new ResponseEntity<>(patient, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(value = "/search/{nuip}", method = RequestMethod.GET)
	public ResponseEntity<Patient> findByNuip(@PathVariable(NUIP_PARAM) String nuip) {
		return patientService.findByNuip(nuip).map(patient -> new ResponseEntity<>(patient, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Patient> save(@RequestBody Patient patient) {
		Patient persistedPatient = patientService.save(patient);
		return new ResponseEntity<>(persistedPatient, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<Patient> update(@RequestBody Patient patient) {
		Optional<Patient> p = patientService.find(patient.getId());

		if (p.isPresent())
			patient.setCompany(p.get().getCompany());

		Patient persistedPatient = patientService.update(patient);
		return new ResponseEntity<>(persistedPatient, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{patientId}", method = RequestMethod.DELETE)
	public ResponseEntity<Patient> delete(@PathVariable(PATIENT_ID_PARAM) Long patientId) {
		patientService.delete(patientId);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
}
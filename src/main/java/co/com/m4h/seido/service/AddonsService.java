package co.com.m4h.seido.service;

import java.io.File;
import java.util.List;

import co.com.m4h.seido.json.Control6Meses;

/**
 * Created by Jose Molina on 12/2/18.
 */
public interface AddonsService {
	List<Control6Meses> findPatientsToControl();

	File getExcel();
}
package co.com.m4h.seido.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.com.m4h.seido.common.Constant;
import co.com.m4h.seido.json.Control6Meses;
import co.com.m4h.seido.service.AddonsService;

/**
 * Created by Jose Molina on 12/2/18.
 */
@RestController
@RequestMapping(value = "/addons", produces = Constant.CONTENT_TYPE_JSON)
public class AddonsController {

	@Autowired
	private AddonsService addonsService;

	@RequestMapping(value = "/control6", method = RequestMethod.GET)
	public ResponseEntity<List<Control6Meses>> findPatientsToControl() {
		List<Control6Meses> controls = addonsService.findPatientsToControl();
		return new ResponseEntity<>(controls, HttpStatus.OK);
	}

	@RequestMapping(value = "/control6excel", method = RequestMethod.GET)
	public void buildExcel(HttpServletResponse response) {

		try {
			File file = addonsService.getExcel();

			response.addHeader("Content-Disposition", "attachment; filename=" + file.getName());
			response.setContentType(Files.probeContentType(file.toPath()));
			response.setContentLengthLong(file.length());

			FileInputStream fis = new FileInputStream(file);
			int c;
			while ((c = fis.read()) > -1) {
				response.getOutputStream().write(c);
			}

			response.flushBuffer();

			fis.close();
			file.delete();
		} catch (IOException e) {
			System.out.println("::::: error: " + e.getMessage());
		}

	}
}
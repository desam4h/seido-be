package co.com.m4h.seido.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.com.m4h.seido.common.Constant;
import co.com.m4h.seido.model.User;
import co.com.m4h.seido.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Jose Molina on 2/1/18.
 */
@RestController
@Slf4j
@RequestMapping(value = "/users", produces = Constant.CONTENT_TYPE_JSON)
public class UserController {
	// consumes = Constant.CONTENT_TYPE_JSON,

	private static final String USER_ID_PARAM = "userId";

	private static final String USERNAME_PARAM = "username";

	@Autowired
	private UserService userService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<User>> findAll() {
		List<User> users = userService.findAll();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	public ResponseEntity<User> find(@PathVariable(USER_ID_PARAM) Long userId) {
		return userService.find(userId).map(user -> new ResponseEntity<>(user, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(value = "/search/{username}", method = RequestMethod.GET)
	public ResponseEntity<User> findByUsername(@PathVariable(USERNAME_PARAM) String username) {
		return userService.findByUsername(username).map(user -> new ResponseEntity<>(user, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<User> save(@RequestBody User user) {
		User persistedUser = userService.save(user);
		return new ResponseEntity<>(persistedUser, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<User> update(@RequestBody User user) {
		// Optional<User> u = userService.find(user.getId());

		// if (u.isPresent())
		// user.setCompany(u.get().getCompany());

		User persistedUser = userService.update(user);
		return new ResponseEntity<>(persistedUser, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
	public ResponseEntity<User> delete(@PathVariable(USER_ID_PARAM) Long userId) {
		try {
			userService.delete(userId);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		} catch (Exception e) {
			log.warn(e.getMessage());
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
}
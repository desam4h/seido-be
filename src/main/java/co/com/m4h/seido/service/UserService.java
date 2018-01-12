package co.com.m4h.seido.service;

import java.util.List;
import java.util.Optional;

import co.com.m4h.seido.model.User;

/**
 * Created by Jose Molina on 2/1/18.
 */
public interface UserService extends GenericCrud<User> {

	List<User> findAll();

	Optional<User> findByUsername(String username);
}
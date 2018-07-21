package co.com.m4h.seido.service.impl;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.com.m4h.seido.common.SecurityUtil;
import co.com.m4h.seido.model.User;
import co.com.m4h.seido.persistence.UserRepository;
import co.com.m4h.seido.service.UserService;

/**
 * Created by Jose Molina on 2/1/18.
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Transactional
	@Override
	public User save(User user) {
		user.setUsername(user.getUsername().toLowerCase());
		user.setLastPasswordResetDate(new Date(System.currentTimeMillis()));

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		final User persistedUser = userRepository.save(user);
		return persistedUser;
	}

	@Override
	public User update(User user) {
		user.setUsername(user.getUsername().toLowerCase());
		user.setLastPasswordResetDate(new Date(System.currentTimeMillis()));

		User savedUser = userRepository.findOneByUsername(user.getUsername());

		if (!savedUser.getPassword().equals(user.getPassword())) {
			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}

		return userRepository.save(user);
	}

	@Override
	public Optional<User> find(Long userId) {
		return Optional.ofNullable(userRepository.findOne(userId));
	}

	@Override
	@Transactional
	public void delete(Long userId) {
		if (SecurityUtil.getUserId().equals(userId))
			throw new EmptyResultDataAccessException("No se puede eliminar el mismo usuario", 0);
		else
			userRepository.delete(userId);
	}

	@Override
	public List<User> findAll() {
		String role = SecurityUtil.getRole();

		if (role.equals("ROLE_ROOT")) {

			return userRepository.findAll();
		} else {

			Long companyId = SecurityUtil.getCompanyId();

			return userRepository.findAllByCompanyId(companyId);
		}
	}

	@Override
	public Optional<User> findByUsername(String username) {
		username = username.toLowerCase();
		String role = SecurityUtil.getRole();

		if (role.equals("ROLE_ROOT")) {
			User user = userRepository.findOneByUsername(username);

			return Optional.ofNullable(user);
		} else {
			Long companyId = SecurityUtil.getCompanyId();
			User user = userRepository.findOneByCompanyIdAndUsername(companyId, username);

			return Optional.ofNullable(user);
		}

	}
}

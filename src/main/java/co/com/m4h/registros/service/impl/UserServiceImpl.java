package co.com.m4h.registros.service.impl;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.com.m4h.registros.model.User;
import co.com.m4h.registros.persistence.UserRepository;
import co.com.m4h.registros.service.UserService;

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
		user.setLastPasswordResetDate(new Date(System.currentTimeMillis()));

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		final User persistedUser = userRepository.save(user);
		return persistedUser;
	}

	@Override
	public User update(User user) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		return userRepository.save(user);
	}

	@Override
	public Optional<User> find(Long userId) {
		return Optional.ofNullable(userRepository.findOne(userId));
	}

	@Override
	@Transactional
	public void delete(Long userId) {
		// TODO: Add security check against the company

		userRepository.delete(userId);
	}

	@Override
	public List<User> findAll() {
		// Long companyId = SecurityUtil.getCompanyId();
		return userRepository.findAll();
	}

	@Override
	public Optional<User> findByUsername(String username) {
		// TODO: Pendiente
		User user = userRepository.findByUsername(username);
		return Optional.ofNullable(user);
	}
}

package co.com.m4h.registros.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.m4h.registros.model.security.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}

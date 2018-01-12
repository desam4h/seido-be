package co.com.m4h.seido.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.m4h.seido.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findAllByCompanyId(Long companyId);

	User findOneByCompanyIdAndUsername(Long companyId, String username);

	User findOneByUsername(String username);
}

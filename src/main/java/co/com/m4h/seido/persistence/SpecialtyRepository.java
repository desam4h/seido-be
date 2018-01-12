package co.com.m4h.seido.persistence;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import co.com.m4h.seido.model.Specialty;

@Repository
public interface SpecialtyRepository extends PagingAndSortingRepository<Specialty, Long> {
	List<Specialty> findAllByCompanyId(Long companyId);

	List<Specialty> findAll();
}

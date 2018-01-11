package co.com.m4h.registros.persistence;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import co.com.m4h.registros.model.Company;

@Repository
public interface CompanyRepository extends PagingAndSortingRepository<Company, Long> {
	List<Company> findAll();
}

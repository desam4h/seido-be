package co.com.m4h.seido.persistence;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import co.com.m4h.seido.model.Company;

@Repository
public interface CompanyRepository extends PagingAndSortingRepository<Company, Long> {
	List<Company> findAll();
}

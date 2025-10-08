package com.farmer.Form.Repository;

import com.farmer.Form.Entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByShortName(String shortName);
    boolean existsByName(String name);
    boolean existsByShortName(String shortName);
    boolean existsByEmail(String email);
}



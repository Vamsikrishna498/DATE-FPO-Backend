package com.farmer.Form.Repository;

import com.farmer.Form.Entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    
    Optional<Country> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT c FROM Country c LEFT JOIN FETCH c.states WHERE c.id = :id")
    Optional<Country> findByIdWithStates(@Param("id") Long id);
    
    @Query("SELECT c FROM Country c LEFT JOIN FETCH c.states")
    List<Country> findAllWithStates();
}
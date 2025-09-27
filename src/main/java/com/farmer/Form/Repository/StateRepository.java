package com.farmer.Form.Repository;

import com.farmer.Form.Entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
    
    List<State> findByCountryId(Long countryId);
    
    Optional<State> findByNameAndCountryId(String name, Long countryId);
    
    boolean existsByNameAndCountryId(String name, Long countryId);
    
    @Query("SELECT s FROM State s LEFT JOIN FETCH s.districts WHERE s.id = :id")
    Optional<State> findByIdWithDistricts(@Param("id") Long id);
    
    @Query("SELECT s FROM State s LEFT JOIN FETCH s.districts WHERE s.country.id = :countryId")
    List<State> findByCountryIdWithDistricts(@Param("countryId") Long countryId);
}
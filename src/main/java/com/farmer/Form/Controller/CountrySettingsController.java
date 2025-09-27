package com.farmer.Form.Controller;

import com.farmer.Form.DTO.*;
import com.farmer.Form.Service.CountrySettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/country-settings")
@RequiredArgsConstructor
@Slf4j
public class CountrySettingsController {
    
    private final CountrySettingsService countrySettingsService;
    
    // Country endpoints
    @PostMapping("/country")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<CountryDTO> createCountry(@Valid @RequestBody CountryCreationDTO creationDTO) {
        log.info("Creating country: {}", creationDTO.getName());
        CountryDTO createdCountry = countrySettingsService.createCountry(creationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCountry);
    }
    
    @GetMapping("/countries")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<CountryDTO>> getAllCountries() {
        log.info("Fetching all countries");
        List<CountryDTO> countries = countrySettingsService.getAllCountries();
        return ResponseEntity.ok(countries);
    }
    
    @GetMapping("/country/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<CountryDTO> getCountryById(@PathVariable Long id) {
        log.info("Fetching country with id: {}", id);
        CountryDTO country = countrySettingsService.getCountryById(id);
        return ResponseEntity.ok(country);
    }
    
    @PutMapping("/country/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<CountryDTO> updateCountry(@PathVariable Long id, @Valid @RequestBody CountryCreationDTO updateDTO) {
        log.info("Updating country with id: {}", id);
        CountryDTO updatedCountry = countrySettingsService.updateCountry(id, updateDTO);
        return ResponseEntity.ok(updatedCountry);
    }
    
    @DeleteMapping("/country/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
        log.info("Deleting country with id: {}", id);
        countrySettingsService.deleteCountry(id);
        return ResponseEntity.noContent().build();
    }
    
    // State endpoints
    @PostMapping("/state")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<StateDTO> createState(@Valid @RequestBody StateCreationDTO creationDTO) {
        log.info("Creating state: {} for country id: {}", creationDTO.getName(), creationDTO.getCountryId());
        StateDTO createdState = countrySettingsService.createState(creationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdState);
    }
    
    @GetMapping("/states/{countryId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<StateDTO>> getStatesByCountryId(@PathVariable Long countryId) {
        log.info("Fetching states for country id: {}", countryId);
        List<StateDTO> states = countrySettingsService.getStatesByCountryId(countryId);
        log.info("Found {} states for country id: {}", states.size(), countryId);
        return ResponseEntity.ok(states);
    }
    
    @GetMapping("/state/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<StateDTO> getStateById(@PathVariable Long id) {
        log.info("Fetching state with id: {}", id);
        StateDTO state = countrySettingsService.getStateById(id);
        return ResponseEntity.ok(state);
    }
    
    @PutMapping("/state/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<StateDTO> updateState(@PathVariable Long id, @Valid @RequestBody StateCreationDTO updateDTO) {
        log.info("Updating state with id: {}", id);
        StateDTO updatedState = countrySettingsService.updateState(id, updateDTO);
        return ResponseEntity.ok(updatedState);
    }
    
    @DeleteMapping("/state/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteState(@PathVariable Long id) {
        log.info("Deleting state with id: {}", id);
        countrySettingsService.deleteState(id);
        return ResponseEntity.noContent().build();
    }
    
    // District endpoints
    @PostMapping("/district")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<DistrictDTO> createDistrict(@Valid @RequestBody DistrictCreationDTO creationDTO) {
        log.info("Creating district: {} for state id: {}", creationDTO.getName(), creationDTO.getStateId());
        DistrictDTO createdDistrict = countrySettingsService.createDistrict(creationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDistrict);
    }
    
    @GetMapping("/districts/{stateId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<DistrictDTO>> getDistrictsByStateId(@PathVariable Long stateId) {
        log.info("Fetching districts for state id: {}", stateId);
        List<DistrictDTO> districts = countrySettingsService.getDistrictsByStateId(stateId);
        return ResponseEntity.ok(districts);
    }
    
    @GetMapping("/district/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<DistrictDTO> getDistrictById(@PathVariable Long id) {
        log.info("Fetching district with id: {}", id);
        DistrictDTO district = countrySettingsService.getDistrictById(id);
        return ResponseEntity.ok(district);
    }
    
    @PutMapping("/district/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<DistrictDTO> updateDistrict(@PathVariable Long id, @Valid @RequestBody DistrictCreationDTO updateDTO) {
        log.info("Updating district with id: {}", id);
        DistrictDTO updatedDistrict = countrySettingsService.updateDistrict(id, updateDTO);
        return ResponseEntity.ok(updatedDistrict);
    }
    
    @DeleteMapping("/district/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteDistrict(@PathVariable Long id) {
        log.info("Deleting district with id: {}", id);
        countrySettingsService.deleteDistrict(id);
        return ResponseEntity.noContent().build();
    }
    
    // Block endpoints
    @PostMapping("/block")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<BlockDTO> createBlock(@Valid @RequestBody BlockCreationDTO creationDTO) {
        log.info("Creating block: {} for district id: {}", creationDTO.getName(), creationDTO.getDistrictId());
        BlockDTO createdBlock = countrySettingsService.createBlock(creationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBlock);
    }
    
    @GetMapping("/blocks/{districtId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<BlockDTO>> getBlocksByDistrictId(@PathVariable Long districtId) {
        log.info("Fetching blocks for district id: {}", districtId);
        List<BlockDTO> blocks = countrySettingsService.getBlocksByDistrictId(districtId);
        return ResponseEntity.ok(blocks);
    }
    
    @GetMapping("/block/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<BlockDTO> getBlockById(@PathVariable Long id) {
        log.info("Fetching block with id: {}", id);
        BlockDTO block = countrySettingsService.getBlockById(id);
        return ResponseEntity.ok(block);
    }
    
    @PutMapping("/block/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<BlockDTO> updateBlock(@PathVariable Long id, @Valid @RequestBody BlockCreationDTO updateDTO) {
        log.info("Updating block with id: {}", id);
        BlockDTO updatedBlock = countrySettingsService.updateBlock(id, updateDTO);
        return ResponseEntity.ok(updatedBlock);
    }
    
    @DeleteMapping("/block/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteBlock(@PathVariable Long id) {
        log.info("Deleting block with id: {}", id);
        countrySettingsService.deleteBlock(id);
        return ResponseEntity.noContent().build();
    }
    
    // Village endpoints
    @PostMapping("/village")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<VillageDTO> createVillage(@Valid @RequestBody VillageCreationDTO creationDTO) {
        log.info("Creating village: {} for block id: {}", creationDTO.getName(), creationDTO.getBlockId());
        VillageDTO createdVillage = countrySettingsService.createVillage(creationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVillage);
    }
    
    @GetMapping("/villages/{blockId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<VillageDTO>> getVillagesByBlockId(@PathVariable Long blockId) {
        log.info("Fetching villages for block id: {}", blockId);
        List<VillageDTO> villages = countrySettingsService.getVillagesByBlockId(blockId);
        return ResponseEntity.ok(villages);
    }
    
    @GetMapping("/village/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<VillageDTO> getVillageById(@PathVariable Long id) {
        log.info("Fetching village with id: {}", id);
        VillageDTO village = countrySettingsService.getVillageById(id);
        return ResponseEntity.ok(village);
    }
    
    @PutMapping("/village/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<VillageDTO> updateVillage(@PathVariable Long id, @Valid @RequestBody VillageCreationDTO updateDTO) {
        log.info("Updating village with id: {}", id);
        VillageDTO updatedVillage = countrySettingsService.updateVillage(id, updateDTO);
        return ResponseEntity.ok(updatedVillage);
    }
    
    @DeleteMapping("/village/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteVillage(@PathVariable Long id) {
        log.info("Deleting village with id: {}", id);
        countrySettingsService.deleteVillage(id);
        return ResponseEntity.noContent().build();
    }
    
    // Zipcode endpoints
    @PostMapping("/zipcode")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ZipcodeDTO> createZipcode(@Valid @RequestBody ZipcodeCreationDTO creationDTO) {
        log.info("Creating zipcode: {} for village id: {}", creationDTO.getCode(), creationDTO.getVillageId());
        ZipcodeDTO createdZipcode = countrySettingsService.createZipcode(creationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdZipcode);
    }
    
    @GetMapping("/zipcodes/{villageId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<ZipcodeDTO>> getZipcodesByVillageId(@PathVariable Long villageId) {
        log.info("Fetching zipcodes for village id: {}", villageId);
        List<ZipcodeDTO> zipcodes = countrySettingsService.getZipcodesByVillageId(villageId);
        return ResponseEntity.ok(zipcodes);
    }
    
    @GetMapping("/zipcode/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ZipcodeDTO> getZipcodeById(@PathVariable Long id) {
        log.info("Fetching zipcode with id: {}", id);
        ZipcodeDTO zipcode = countrySettingsService.getZipcodeById(id);
        return ResponseEntity.ok(zipcode);
    }
    
    @PutMapping("/zipcode/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ZipcodeDTO> updateZipcode(@PathVariable Long id, @Valid @RequestBody ZipcodeCreationDTO updateDTO) {
        log.info("Updating zipcode with id: {}", id);
        ZipcodeDTO updatedZipcode = countrySettingsService.updateZipcode(id, updateDTO);
        return ResponseEntity.ok(updatedZipcode);
    }
    
    @DeleteMapping("/zipcode/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteZipcode(@PathVariable Long id) {
        log.info("Deleting zipcode with id: {}", id);
        countrySettingsService.deleteZipcode(id);
        return ResponseEntity.noContent().build();
    }
    
    // Search endpoint
    @GetMapping("/zipcodes/search/{code}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ZipcodeDTO> searchZipcodeWithHierarchy(@PathVariable String code) {
        log.info("Searching zipcode with code: {}", code);
        ZipcodeDTO zipcode = countrySettingsService.searchZipcodeWithHierarchy(code);
        return ResponseEntity.ok(zipcode);
    }
}

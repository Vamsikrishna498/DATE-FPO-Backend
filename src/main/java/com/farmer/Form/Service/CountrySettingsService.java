package com.farmer.Form.Service;

import com.farmer.Form.DTO.*;
import com.farmer.Form.Entity.Zipcode;

import java.util.List;

public interface CountrySettingsService {
    
    // Country operations
    CountryDTO createCountry(CountryCreationDTO creationDTO);
    List<CountryDTO> getAllCountries();
    CountryDTO getCountryById(Long id);
    CountryDTO updateCountry(Long id, CountryCreationDTO updateDTO);
    void deleteCountry(Long id);
    
    // State operations
    StateDTO createState(StateCreationDTO creationDTO);
    List<StateDTO> getStatesByCountryId(Long countryId);
    StateDTO getStateById(Long id);
    StateDTO updateState(Long id, StateCreationDTO updateDTO);
    void deleteState(Long id);
    
    // District operations
    DistrictDTO createDistrict(DistrictCreationDTO creationDTO);
    List<DistrictDTO> getDistrictsByStateId(Long stateId);
    DistrictDTO getDistrictById(Long id);
    DistrictDTO updateDistrict(Long id, DistrictCreationDTO updateDTO);
    void deleteDistrict(Long id);
    
    // Block operations
    BlockDTO createBlock(BlockCreationDTO creationDTO);
    List<BlockDTO> getBlocksByDistrictId(Long districtId);
    BlockDTO getBlockById(Long id);
    BlockDTO updateBlock(Long id, BlockCreationDTO updateDTO);
    void deleteBlock(Long id);
    
    // Village operations
    VillageDTO createVillage(VillageCreationDTO creationDTO);
    List<VillageDTO> getVillagesByBlockId(Long blockId);
    VillageDTO getVillageById(Long id);
    VillageDTO updateVillage(Long id, VillageCreationDTO updateDTO);
    void deleteVillage(Long id);
    
    // Zipcode operations
    ZipcodeDTO createZipcode(ZipcodeCreationDTO creationDTO);
    List<ZipcodeDTO> getZipcodesByVillageId(Long villageId);
    ZipcodeDTO getZipcodeById(Long id);
    ZipcodeDTO updateZipcode(Long id, ZipcodeCreationDTO updateDTO);
    void deleteZipcode(Long id);
    
    // Search operations
    ZipcodeDTO searchZipcodeWithHierarchy(String code);
}

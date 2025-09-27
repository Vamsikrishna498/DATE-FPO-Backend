package com.farmer.Form.Service;

import com.farmer.Form.DTO.*;
import com.farmer.Form.Entity.*;
import com.farmer.Form.Repository.*;
import com.farmer.Form.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CountrySettingsServiceImpl implements CountrySettingsService {
    
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final BlockRepository blockRepository;
    private final VillageRepository villageRepository;
    private final ZipcodeRepository zipcodeRepository;
    
    // Country operations
    @Override
    public CountryDTO createCountry(CountryCreationDTO creationDTO) {
        if (countryRepository.existsByName(creationDTO.getName())) {
            throw new IllegalArgumentException("Country with name '" + creationDTO.getName() + "' already exists");
        }
        
        Country country = Country.builder()
                .name(creationDTO.getName())
                .build();
        
        Country savedCountry = countryRepository.save(country);
        log.info("Created country: {}", savedCountry.getName());
        return CountryDTO.fromEntity(savedCountry);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CountryDTO> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(CountryDTO::fromEntity)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public CountryDTO getCountryById(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + id));
        return CountryDTO.fromEntity(country);
    }
    
    @Override
    public CountryDTO updateCountry(Long id, CountryCreationDTO updateDTO) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + id));
        
        if (!country.getName().equals(updateDTO.getName()) && 
            countryRepository.existsByName(updateDTO.getName())) {
            throw new IllegalArgumentException("Country with name '" + updateDTO.getName() + "' already exists");
        }
        
        country.setName(updateDTO.getName());
        Country updatedCountry = countryRepository.save(country);
        log.info("Updated country: {}", updatedCountry.getName());
        return CountryDTO.fromEntity(updatedCountry);
    }
    
    @Override
    public void deleteCountry(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + id));
        
        countryRepository.delete(country);
        log.info("Deleted country: {}", country.getName());
    }
    
    // State operations
    @Override
    public StateDTO createState(StateCreationDTO creationDTO) {
        Country country = countryRepository.findById(creationDTO.getCountryId())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + creationDTO.getCountryId()));
        
        if (stateRepository.existsByNameAndCountryId(creationDTO.getName(), creationDTO.getCountryId())) {
            throw new IllegalArgumentException("State with name '" + creationDTO.getName() + "' already exists in this country");
        }
        
        State state = State.builder()
                .name(creationDTO.getName())
                .country(country)
                .build();
        
        State savedState = stateRepository.save(state);
        log.info("Created state: {} in country: {}", savedState.getName(), country.getName());
        return StateDTO.fromEntity(savedState);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StateDTO> getStatesByCountryId(Long countryId) {
        log.info("Service: Fetching states for country id: {}", countryId);
        List<State> states = stateRepository.findByCountryId(countryId);
        log.info("Service: Found {} states in database for country id: {}", states.size(), countryId);
        return states.stream()
                .map(StateDTO::fromEntity)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public StateDTO getStateById(Long id) {
        State state = stateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State not found with id: " + id));
        return StateDTO.fromEntity(state);
    }
    
    @Override
    public StateDTO updateState(Long id, StateCreationDTO updateDTO) {
        State state = stateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State not found with id: " + id));
        
        Country country = countryRepository.findById(updateDTO.getCountryId())
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + updateDTO.getCountryId()));
        
        if (!state.getName().equals(updateDTO.getName()) && 
            stateRepository.existsByNameAndCountryId(updateDTO.getName(), updateDTO.getCountryId())) {
            throw new IllegalArgumentException("State with name '" + updateDTO.getName() + "' already exists in this country");
        }
        
        state.setName(updateDTO.getName());
        state.setCountry(country);
        State updatedState = stateRepository.save(state);
        log.info("Updated state: {}", updatedState.getName());
        return StateDTO.fromEntity(updatedState);
    }
    
    @Override
    public void deleteState(Long id) {
        State state = stateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State not found with id: " + id));
        
        stateRepository.delete(state);
        log.info("Deleted state: {}", state.getName());
    }
    
    // District operations
    @Override
    public DistrictDTO createDistrict(DistrictCreationDTO creationDTO) {
        State state = stateRepository.findById(creationDTO.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException("State not found with id: " + creationDTO.getStateId()));
        
        if (districtRepository.existsByNameAndStateId(creationDTO.getName(), creationDTO.getStateId())) {
            throw new IllegalArgumentException("District with name '" + creationDTO.getName() + "' already exists in this state");
        }
        
        District district = District.builder()
                .name(creationDTO.getName())
                .state(state)
                .build();
        
        District savedDistrict = districtRepository.save(district);
        log.info("Created district: {} in state: {}", savedDistrict.getName(), state.getName());
        return DistrictDTO.fromEntity(savedDistrict);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DistrictDTO> getDistrictsByStateId(Long stateId) {
        return districtRepository.findByStateId(stateId).stream()
                .map(DistrictDTO::fromEntity)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public DistrictDTO getDistrictById(Long id) {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("District not found with id: " + id));
        return DistrictDTO.fromEntity(district);
    }
    
    @Override
    public DistrictDTO updateDistrict(Long id, DistrictCreationDTO updateDTO) {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("District not found with id: " + id));
        
        State state = stateRepository.findById(updateDTO.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException("State not found with id: " + updateDTO.getStateId()));
        
        if (!district.getName().equals(updateDTO.getName()) && 
            districtRepository.existsByNameAndStateId(updateDTO.getName(), updateDTO.getStateId())) {
            throw new IllegalArgumentException("District with name '" + updateDTO.getName() + "' already exists in this state");
        }
        
        district.setName(updateDTO.getName());
        district.setState(state);
        District updatedDistrict = districtRepository.save(district);
        log.info("Updated district: {}", updatedDistrict.getName());
        return DistrictDTO.fromEntity(updatedDistrict);
    }
    
    @Override
    public void deleteDistrict(Long id) {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("District not found with id: " + id));
        
        districtRepository.delete(district);
        log.info("Deleted district: {}", district.getName());
    }
    
    // Block operations
    @Override
    public BlockDTO createBlock(BlockCreationDTO creationDTO) {
        District district = districtRepository.findById(creationDTO.getDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("District not found with id: " + creationDTO.getDistrictId()));
        
        if (blockRepository.existsByNameAndDistrictId(creationDTO.getName(), creationDTO.getDistrictId())) {
            throw new IllegalArgumentException("Block with name '" + creationDTO.getName() + "' already exists in this district");
        }
        
        Block block = Block.builder()
                .name(creationDTO.getName())
                .district(district)
                .build();
        
        Block savedBlock = blockRepository.save(block);
        log.info("Created block: {} in district: {}", savedBlock.getName(), district.getName());
        return BlockDTO.fromEntity(savedBlock);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BlockDTO> getBlocksByDistrictId(Long districtId) {
        return blockRepository.findByDistrictId(districtId).stream()
                .map(BlockDTO::fromEntity)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public BlockDTO getBlockById(Long id) {
        Block block = blockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Block not found with id: " + id));
        return BlockDTO.fromEntity(block);
    }
    
    @Override
    public BlockDTO updateBlock(Long id, BlockCreationDTO updateDTO) {
        Block block = blockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Block not found with id: " + id));
        
        District district = districtRepository.findById(updateDTO.getDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("District not found with id: " + updateDTO.getDistrictId()));
        
        if (!block.getName().equals(updateDTO.getName()) && 
            blockRepository.existsByNameAndDistrictId(updateDTO.getName(), updateDTO.getDistrictId())) {
            throw new IllegalArgumentException("Block with name '" + updateDTO.getName() + "' already exists in this district");
        }
        
        block.setName(updateDTO.getName());
        block.setDistrict(district);
        Block updatedBlock = blockRepository.save(block);
        log.info("Updated block: {}", updatedBlock.getName());
        return BlockDTO.fromEntity(updatedBlock);
    }
    
    @Override
    public void deleteBlock(Long id) {
        Block block = blockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Block not found with id: " + id));
        
        blockRepository.delete(block);
        log.info("Deleted block: {}", block.getName());
    }
    
    // Village operations
    @Override
    public VillageDTO createVillage(VillageCreationDTO creationDTO) {
        Block block = blockRepository.findById(creationDTO.getBlockId())
                .orElseThrow(() -> new ResourceNotFoundException("Block not found with id: " + creationDTO.getBlockId()));
        
        if (villageRepository.existsByNameAndBlockId(creationDTO.getName(), creationDTO.getBlockId())) {
            throw new IllegalArgumentException("Village with name '" + creationDTO.getName() + "' already exists in this block");
        }
        
        Village village = Village.builder()
                .name(creationDTO.getName())
                .block(block)
                .build();
        
        Village savedVillage = villageRepository.save(village);
        log.info("Created village: {} in block: {}", savedVillage.getName(), block.getName());
        return VillageDTO.fromEntity(savedVillage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<VillageDTO> getVillagesByBlockId(Long blockId) {
        return villageRepository.findByBlockId(blockId).stream()
                .map(VillageDTO::fromEntity)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public VillageDTO getVillageById(Long id) {
        Village village = villageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Village not found with id: " + id));
        return VillageDTO.fromEntity(village);
    }
    
    @Override
    public VillageDTO updateVillage(Long id, VillageCreationDTO updateDTO) {
        Village village = villageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Village not found with id: " + id));
        
        Block block = blockRepository.findById(updateDTO.getBlockId())
                .orElseThrow(() -> new ResourceNotFoundException("Block not found with id: " + updateDTO.getBlockId()));
        
        if (!village.getName().equals(updateDTO.getName()) && 
            villageRepository.existsByNameAndBlockId(updateDTO.getName(), updateDTO.getBlockId())) {
            throw new IllegalArgumentException("Village with name '" + updateDTO.getName() + "' already exists in this block");
        }
        
        village.setName(updateDTO.getName());
        village.setBlock(block);
        Village updatedVillage = villageRepository.save(village);
        log.info("Updated village: {}", updatedVillage.getName());
        return VillageDTO.fromEntity(updatedVillage);
    }
    
    @Override
    public void deleteVillage(Long id) {
        Village village = villageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Village not found with id: " + id));
        
        villageRepository.delete(village);
        log.info("Deleted village: {}", village.getName());
    }
    
    // Zipcode operations
    @Override
    public ZipcodeDTO createZipcode(ZipcodeCreationDTO creationDTO) {
        Village village = villageRepository.findById(creationDTO.getVillageId())
                .orElseThrow(() -> new ResourceNotFoundException("Village not found with id: " + creationDTO.getVillageId()));
        
        if (zipcodeRepository.existsByCodeAndVillageId(creationDTO.getCode(), creationDTO.getVillageId())) {
            throw new IllegalArgumentException("Zipcode '" + creationDTO.getCode() + "' already exists in this village");
        }
        
        Zipcode zipcode = Zipcode.builder()
                .code(creationDTO.getCode())
                .village(village)
                .build();
        
        Zipcode savedZipcode = zipcodeRepository.save(zipcode);
        log.info("Created zipcode: {} in village: {}", savedZipcode.getCode(), village.getName());
        return ZipcodeDTO.fromEntity(savedZipcode);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ZipcodeDTO> getZipcodesByVillageId(Long villageId) {
        return zipcodeRepository.findByVillageId(villageId).stream()
                .map(ZipcodeDTO::fromEntity)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public ZipcodeDTO getZipcodeById(Long id) {
        Zipcode zipcode = zipcodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zipcode not found with id: " + id));
        return ZipcodeDTO.fromEntity(zipcode);
    }
    
    @Override
    public ZipcodeDTO updateZipcode(Long id, ZipcodeCreationDTO updateDTO) {
        Zipcode zipcode = zipcodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zipcode not found with id: " + id));
        
        Village village = villageRepository.findById(updateDTO.getVillageId())
                .orElseThrow(() -> new ResourceNotFoundException("Village not found with id: " + updateDTO.getVillageId()));
        
        if (!zipcode.getCode().equals(updateDTO.getCode()) && 
            zipcodeRepository.existsByCodeAndVillageId(updateDTO.getCode(), updateDTO.getVillageId())) {
            throw new IllegalArgumentException("Zipcode '" + updateDTO.getCode() + "' already exists in this village");
        }
        
        zipcode.setCode(updateDTO.getCode());
        zipcode.setVillage(village);
        Zipcode updatedZipcode = zipcodeRepository.save(zipcode);
        log.info("Updated zipcode: {}", updatedZipcode.getCode());
        return ZipcodeDTO.fromEntity(updatedZipcode);
    }
    
    @Override
    public void deleteZipcode(Long id) {
        Zipcode zipcode = zipcodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zipcode not found with id: " + id));
        
        zipcodeRepository.delete(zipcode);
        log.info("Deleted zipcode: {}", zipcode.getCode());
    }
    
    // Search operations
    @Override
    @Transactional(readOnly = true)
    public ZipcodeDTO searchZipcodeWithHierarchy(String code) {
        Zipcode zipcode = zipcodeRepository.findByCodeWithFullHierarchy(code)
                .orElseThrow(() -> new ResourceNotFoundException("Zipcode not found with code: " + code));
        return ZipcodeDTO.fromEntity(zipcode);
    }
}

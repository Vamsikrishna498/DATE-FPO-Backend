package com.farmer.Form.Service.Impl;

import com.farmer.Form.Entity.Employee;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Entity.IdCard;
import com.farmer.Form.Repository.EmployeeRepository;
import com.farmer.Form.Repository.FarmerRepository;
import com.farmer.Form.Repository.IdCardRepository;
import com.farmer.Form.Service.IdCardPdfService;
import com.farmer.Form.Service.IdCardService;
import com.farmer.Form.Service.IdGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class IdCardServiceImpl implements IdCardService {
	@Autowired
	private IdCardRepository idCardRepository;

	@Autowired
	private FarmerRepository farmerRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private IdGenerationService idGenerationService;

	@Autowired
	private IdCardPdfService idCardPdfService;

	private int calculateAge(LocalDate dob) {
		if (dob == null) return 0;
		return Period.between(dob, LocalDate.now()).getYears();
	}

	@Override
	public IdCard generateFarmerIdCard(Farmer farmer) throws IOException {
		System.out.println("🔄 Generating ID card for farmer: " + farmer.getId() + " - " + farmer.getFirstName() + " " + farmer.getLastName());

		// Strict: only FARMER cards for this holder
		List<IdCard> existingCards = idCardRepository.findByHolderIdAndCardType(farmer.getId().toString(), IdCard.CardType.FARMER);
		if (!existingCards.isEmpty()) {
			IdCard existingCard = existingCards.get(0);
			if (existingCard.getStatus() == IdCard.CardStatus.ACTIVE) {
				existingCard.setPhotoFileName(farmer.getPhotoFileName());
				System.out.println("✅ Updating existing active FARMER ID card photo: " + existingCard.getCardId());
				return idCardRepository.save(existingCard);
			}
		}

		String cardId = idGenerationService.generateFarmerId(farmer.getState(), farmer.getDistrict());

		IdCard idCard = IdCard.builder()
				.cardId(cardId)
				.cardType(IdCard.CardType.FARMER)
				.holderName(farmer.getFirstName() + " " + farmer.getLastName())
				.holderId(farmer.getId().toString())
				.photoFileName(farmer.getPhotoFileName())
				.village(farmer.getVillage())
				.district(farmer.getDistrict())
				.state(farmer.getState())
				.country(farmer.getCountry())
				.age(calculateAge(farmer.getDateOfBirth()))
				.gender(farmer.getGender())
				.dateOfBirth(farmer.getDateOfBirth())
				.status(IdCard.CardStatus.ACTIVE)
				.generatedAt(LocalDateTime.now())
				.expiresAt(LocalDateTime.now().plusYears(5))
				.build();

		idCardPdfService.generateFarmerIdCardPdf(farmer, idCard);
		idCardPdfService.generateFarmerIdCardPng(farmer, idCard);

		String pdfFileName = "idcard_" + cardId + ".pdf";
		String pngFileName = "idcard_" + cardId + ".png";

		idCard.setPdfFileName(pdfFileName);
		idCard.setPngFileName(pngFileName);

		return idCardRepository.save(idCard);
	}

	@Override
	public IdCard generateEmployeeIdCard(Employee employee) throws IOException {
		System.out.println("🔄 Generating ID card for employee: " + employee.getId() + " - " + employee.getFirstName() + " " + employee.getLastName());
		System.out.println("📍 Employee state: " + employee.getState() + ", district: " + employee.getDistrict());

		// Strict: only EMPLOYEE cards for this holder
		List<IdCard> existingCards = idCardRepository.findByHolderIdAndCardType(employee.getId().toString(), IdCard.CardType.EMPLOYEE);
		if (!existingCards.isEmpty()) {
			IdCard existingCard = existingCards.get(0);
			if (existingCard.getStatus() == IdCard.CardStatus.ACTIVE) {
				existingCard.setPhotoFileName(employee.getPhotoFileName());
				System.out.println("✅ Updating existing active EMPLOYEE ID card photo: " + existingCard.getCardId());
				return idCardRepository.save(existingCard);
			}
		}

		String cardId = idGenerationService.generateEmployeeId(employee.getState(), employee.getDistrict());
		System.out.println("🎯 Generated employee ID: " + cardId);

		IdCard idCard = IdCard.builder()
				.cardId(cardId)
				.cardType(IdCard.CardType.EMPLOYEE)
				.holderName(employee.getFirstName() + " " + employee.getLastName())
				.holderId(employee.getId().toString())
				.photoFileName(employee.getPhotoFileName())
				.village(employee.getVillage())
				.district(employee.getDistrict())
				.state(employee.getState())
				.country(employee.getCountry())
				.age(calculateAge(employee.getDob()))
				.gender(employee.getGender())
				.dateOfBirth(employee.getDob())
				.status(IdCard.CardStatus.ACTIVE)
				.generatedAt(LocalDateTime.now())
				.expiresAt(LocalDateTime.now().plusYears(5))
				.build();

		idCardPdfService.generateEmployeeIdCardPdf(employee, idCard);
		idCardPdfService.generateEmployeeIdCardPng(employee, idCard);

		String pdfFileName = "idcard_" + cardId + ".pdf";
		String pngFileName = "idcard_" + cardId + ".png";

		idCard.setPdfFileName(pdfFileName);
		idCard.setPngFileName(pngFileName);

		return idCardRepository.save(idCard);
	}

	@Override
	public Optional<IdCard> getById(String cardId) {
		return idCardRepository.findByCardId(cardId);
	}

	@Override
	public List<IdCard> getByHolderId(String holderId) {
		return idCardRepository.findByHolderId(holderId);
	}

	@Override
	public Page<IdCard> getAllIdCards(Pageable pageable) {
		return idCardRepository.findAll(pageable);
	}

	@Override
	public Page<IdCard> getIdCardsByType(IdCard.CardType cardType, Pageable pageable) {
		List<IdCard> list = idCardRepository.findByCardType(cardType);
		return new PageImpl<>(list, pageable, list.size());
	}

	@Override
	public Page<IdCard> searchIdCardsByName(String name, IdCard.CardType cardType, Pageable pageable) {
		List<IdCard> list = idCardRepository.findByHolderNameContainingAndType(name, cardType);
		return new PageImpl<>(list, pageable, list.size());
	}

	@Override
	public Page<IdCard> getIdCardsByState(String state, IdCard.CardType cardType, Pageable pageable) {
		List<IdCard> list = idCardRepository.findByStateAndType(state, cardType);
		return new PageImpl<>(list, pageable, list.size());
	}

	@Override
	public Page<IdCard> getIdCardsByDistrict(String district, IdCard.CardType cardType, Pageable pageable) {
		List<IdCard> list = idCardRepository.findByDistrictAndType(district, cardType);
		return new PageImpl<>(list, pageable, list.size());
	}

	@Override
	public byte[] downloadIdCardPdf(String cardId) throws IOException {
		Optional<IdCard> idCardOpt = idCardRepository.findByCardId(cardId);
		if (idCardOpt.isEmpty()) throw new RuntimeException("ID card not found");
		IdCard idCard = idCardOpt.get();
		if (idCard.getCardType() == IdCard.CardType.FARMER) {
			Farmer farmer = farmerRepository.findById(Long.parseLong(idCard.getHolderId())).orElseThrow(() -> new RuntimeException("Farmer not found"));
			return idCardPdfService.generateFarmerIdCardPdf(farmer, idCard);
		} else {
			Employee employee = employeeRepository.findById(Long.parseLong(idCard.getHolderId())).orElseThrow(() -> new RuntimeException("Employee not found"));
			return idCardPdfService.generateEmployeeIdCardPdf(employee, idCard);
		}
	}

	@Override
	public byte[] downloadIdCardPng(String cardId) throws IOException {
		Optional<IdCard> idCardOpt = idCardRepository.findByCardId(cardId);
		if (idCardOpt.isEmpty()) throw new RuntimeException("ID card not found");
		IdCard idCard = idCardOpt.get();
		if (idCard.getCardType() == IdCard.CardType.FARMER) {
			Farmer farmer = farmerRepository.findById(Long.parseLong(idCard.getHolderId())).orElseThrow(() -> new RuntimeException("Farmer not found"));
			return idCardPdfService.generateFarmerIdCardPng(farmer, idCard);
		} else {
			Employee employee = employeeRepository.findById(Long.parseLong(idCard.getHolderId())).orElseThrow(() -> new RuntimeException("Employee not found"));
			return idCardPdfService.generateEmployeeIdCardPng(employee, idCard);
		}
	}

	@Override
	public IdCard regenerateIdCard(String cardId) throws IOException {
		Optional<IdCard> idCardOpt = idCardRepository.findByCardId(cardId);
		if (idCardOpt.isEmpty()) throw new RuntimeException("ID card not found");
		IdCard idCard = idCardOpt.get();
		if (idCard.getCardType() == IdCard.CardType.FARMER) {
			Farmer farmer = farmerRepository.findById(Long.parseLong(idCard.getHolderId())).orElseThrow(() -> new RuntimeException("Farmer not found"));
			return generateFarmerIdCard(farmer);
		} else {
			Employee employee = employeeRepository.findById(Long.parseLong(idCard.getHolderId())).orElseThrow(() -> new RuntimeException("Employee not found"));
			return generateEmployeeIdCard(employee);
		}
	}

	@Override
	public IdCard revokeIdCard(String cardId) {
		Optional<IdCard> idCardOpt = idCardRepository.findByCardId(cardId);
		if (idCardOpt.isEmpty()) throw new RuntimeException("ID card not found");
		IdCard idCard = idCardOpt.get();
		idCard.setStatus(IdCard.CardStatus.REVOKED);
		return idCardRepository.save(idCard);
	}

	@Override
	public IdCardStatistics getIdCardStatistics() {
		long totalIdCards = idCardRepository.count();
		long farmerIdCards = idCardRepository.countByTypeAndState(IdCard.CardType.FARMER, null);
		long employeeIdCards = idCardRepository.countByTypeAndState(IdCard.CardType.EMPLOYEE, null);
		long activeIdCards = idCardRepository.countByStatus(IdCard.CardStatus.ACTIVE);
		long expiredIdCards = idCardRepository.countByStatus(IdCard.CardStatus.EXPIRED);
		long revokedIdCards = idCardRepository.countByStatus(IdCard.CardStatus.REVOKED);
		return new IdCardStatistics(totalIdCards, farmerIdCards, employeeIdCards, activeIdCards, expiredIdCards, revokedIdCards);
	}
}

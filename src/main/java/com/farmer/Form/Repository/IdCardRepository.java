package com.farmer.Form.Repository;

import com.farmer.Form.Entity.IdCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IdCardRepository extends JpaRepository<IdCard, Long> {
    
    Optional<IdCard> findByCardId(String cardId);
    
    List<IdCard> findByCardType(IdCard.CardType cardType);
    
    List<IdCard> findByHolderId(String holderId);
    
    List<IdCard> findByStatus(IdCard.CardStatus status);
    
    @Query("SELECT ic FROM IdCard ic WHERE ic.cardType = :cardType AND ic.status = 'ACTIVE'")
    List<IdCard> findActiveCardsByType(@Param("cardType") IdCard.CardType cardType);
    
    @Query("SELECT ic FROM IdCard ic WHERE ic.state = :state AND ic.cardType = :cardType")
    List<IdCard> findByStateAndType(@Param("state") String state, @Param("cardType") IdCard.CardType cardType);
    
    @Query("SELECT ic FROM IdCard ic WHERE ic.district = :district AND ic.cardType = :cardType")
    List<IdCard> findByDistrictAndType(@Param("district") String district, @Param("cardType") IdCard.CardType cardType);
    
    @Query("SELECT COUNT(ic) FROM IdCard ic WHERE ic.cardType = :cardType AND ic.state = :state")
    Long countByTypeAndState(@Param("cardType") IdCard.CardType cardType, @Param("state") String state);
    
    @Query("SELECT ic FROM IdCard ic WHERE ic.holderName LIKE %:name% AND ic.cardType = :cardType")
    List<IdCard> findByHolderNameContainingAndType(@Param("name") String name, @Param("cardType") IdCard.CardType cardType);
    
    Long countByStatus(IdCard.CardStatus status);

	// New: strictly fetch by holder and card type to avoid cross-type collisions
	List<IdCard> findByHolderIdAndCardType(String holderId, IdCard.CardType cardType);
}

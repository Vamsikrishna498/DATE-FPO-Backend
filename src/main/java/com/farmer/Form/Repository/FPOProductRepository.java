package com.farmer.Form.Repository;

import com.farmer.Form.Entity.FPOProduct;
import com.farmer.Form.Entity.FPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FPOProductRepository extends JpaRepository<FPOProduct, Long> {

    List<FPOProduct> findByFpo(FPO fpo);

    List<FPOProduct> findByFpoAndStatus(FPO fpo, FPOProduct.ProductStatus status);

    @Query("SELECT fp FROM FPOProduct fp WHERE fp.fpo.id = :fpoId")
    List<FPOProduct> findByFpoId(@Param("fpoId") Long fpoId);

    @Query("SELECT fp FROM FPOProduct fp WHERE fp.fpo.id = :fpoId AND fp.category.id = :categoryId")
    List<FPOProduct> findByFpoIdAndCategoryId(@Param("fpoId") Long fpoId, @Param("categoryId") Long categoryId);

    @Query("SELECT fp FROM FPOProduct fp WHERE fp.fpo.id = :fpoId AND fp.status = :status")
    List<FPOProduct> findByFpoIdAndStatus(@Param("fpoId") Long fpoId, @Param("status") FPOProduct.ProductStatus status);

    @Query("SELECT fp FROM FPOProduct fp WHERE fp.fpo.id = :fpoId AND " +
           "(:productName IS NULL OR :productName = '' OR LOWER(fp.productName) LIKE LOWER(CONCAT('%', :productName, '%'))) AND " +
           "(:categoryId IS NULL OR fp.category.id = :categoryId) AND " +
           "(:status IS NULL OR fp.status = :status) AND " +
           "(:minPrice IS NULL OR fp.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR fp.price <= :maxPrice)")
    Page<FPOProduct> findProductsWithFilters(
            @Param("fpoId") Long fpoId,
            @Param("productName") String productName,
            @Param("categoryId") Long categoryId,
            @Param("status") FPOProduct.ProductStatus status,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable);

    @Query("SELECT COUNT(fp) FROM FPOProduct fp WHERE fp.fpo.id = :fpoId AND fp.status = :status")
    Long countByFpoIdAndStatus(@Param("fpoId") Long fpoId, @Param("status") FPOProduct.ProductStatus status);

    @Query("SELECT COUNT(fp) FROM FPOProduct fp WHERE fp.fpo.id = :fpoId AND fp.stockQuantity <= fp.minimumStock")
    Long countLowStockProductsByFpoId(@Param("fpoId") Long fpoId);

    @Query("SELECT fp FROM FPOProduct fp WHERE fp.fpo.id = :fpoId AND fp.stockQuantity <= fp.minimumStock")
    List<FPOProduct> findLowStockProductsByFpoId(@Param("fpoId") Long fpoId);
}

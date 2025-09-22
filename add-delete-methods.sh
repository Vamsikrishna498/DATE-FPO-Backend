#!/bin/bash

# Add deleteByFpoId method to all FPO-related repositories

repositories=(
    "FPOCropRepository.java"
    "FPOTurnoverRepository.java"
    "FPOInputShopRepository.java"
    "FPOProductCategoryRepository.java"
    "FPOProductRepository.java"
    "FPONotificationRepository.java"
)

for repo in "${repositories[@]}"; do
    echo "Processing $repo..."
    
    # Add imports
    sed -i '/import org.springframework.data.jpa.repository.Query;/a import org.springframework.data.jpa.repository.Modifying;\nimport org.springframework.transaction.annotation.Transactional;' "src/main/java/com/farmer/Form/Repository/$repo"
    
    # Add deleteByFpoId method before the closing brace
    sed -i '/^}$/i\    @Modifying\n    @Transactional\n    @Query("DELETE FROM '${repo%.java}' e WHERE e.fpo.id = :fpoId")\n    void deleteByFpoId(@Param("fpoId") Long fpoId);' "src/main/java/com/farmer/Form/Repository/$repo"
done

echo "Done adding deleteByFpoId methods to all repositories."

package com.app.my_project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.my_project.entity.ProductionEntity;
import com.app.my_project.entity.SaleTempEntity;
import com.app.my_project.repository.ProductionRepository;
import com.app.my_project.repository.SaleTempRepository;

/**
 * SaleTempService
 * This service handles logic for adding products to the temporary sale cart.
 */
@Service // Marks this class as a Service Component for Spring
public class SaleTempService {

    @Autowired
    private SaleTempRepository saleTempRepository; // Inject SaleTempRepository

    @Autowired
    private ProductionRepository productionRepository; // Inject ProductionRepository

    /**
     * Add a product to the temporary sale cart.
     * If the product already exists for this user, increase the quantity.
     * Otherwise, create a new SaleTempEntity.
     *
     * @param productionId The ID of the product to add
     * @param userId       The ID of the user
     * @return The updated or newly created SaleTempEntity
     */
    @Transactional // Important for managing database transactions
    public SaleTempEntity addProductToSaleTemp(Long productionId, Long userId) {
        // 1. Check if the ProductionEntity exists
        ProductionEntity productionEntity = productionRepository.findById(productionId)
                .orElseThrow(() -> new RuntimeException("Production not found with ID: " + productionId));

        // 2. Find existing SaleTempEntity for this productionId and userId
        SaleTempEntity oldSaleTemp = saleTempRepository.findByProductionIdAndUserId(productionId, userId);

        // 3. If it exists, update the quantity (qty)
        if (oldSaleTemp != null) {
            oldSaleTemp.setQty(oldSaleTemp.getQty() + 1);
            return saleTempRepository.save(oldSaleTemp);
        } else {
            // 4. If not, create a new SaleTempEntity
            SaleTempEntity newSaleTemp = new SaleTempEntity();
            newSaleTemp.setQty(1);
            newSaleTemp.setUserId(userId);
            newSaleTemp.setPrice(productionEntity.getPrice()); // Set price from ProductionEntity
            newSaleTemp.setProduction(productionEntity); // Link to ProductionEntity

            return saleTempRepository.save(newSaleTemp);
        }
    }
}
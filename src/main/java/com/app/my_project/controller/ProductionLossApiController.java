package com.app.my_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import com.app.my_project.entity.ProductionLossEntity;
import com.app.my_project.repository.ProductionLossRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/production-loss")
public class ProductionLossApiController {
    @Autowired
    private ProductionLossRepository productionLossRepository;

    @GetMapping("/{productionId}")
    public List<ProductionLossEntity> getAllProductionLoss(@PathVariable Long productionId) {
        return productionLossRepository.findAllByProductionIdOrderByIdDesc(productionId);
    }

    @PostMapping
    public ProductionLossEntity createProductionLoss(@RequestBody ProductionLossEntity productionLoss) {
        validate(productionLoss);
        // Stamped by the server: the audit trail of when this was really keyed in
        productionLoss.setRecordedAt(LocalDateTime.now());
        return productionLossRepository.save(productionLoss);
    }

    @PutMapping("/{id}")
    public ProductionLossEntity updateProductionLoss(
            @PathVariable Long id,
            @RequestBody ProductionLossEntity productionLoss) {
        validate(productionLoss);

        ProductionLossEntity p = productionLossRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductionLoss not found with id " + id));
        p.setProduction(productionLoss.getProduction());
        p.setRemark(productionLoss.getRemark());
        p.setQty(productionLoss.getQty());
        p.setProductionDate(productionLoss.getProductionDate());
        // recordedAt is deliberately left alone - it records the original entry

        return productionLossRepository.save(p);
    }

    private void validate(ProductionLossEntity productionLoss) {
        if (productionLoss.getQty() == null || productionLoss.getQty() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (productionLoss.getProductionDate() == null) {
            throw new IllegalArgumentException("Production date is required");
        }
        if (productionLoss.getProductionDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Production date cannot be in the future");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteProductionLoss(@PathVariable Long id) {
        productionLossRepository.deleteById(id);
    }
}

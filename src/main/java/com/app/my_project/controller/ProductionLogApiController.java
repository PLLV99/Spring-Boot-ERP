package com.app.my_project.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.my_project.entity.ProductionLogEntity;
import com.app.my_project.repository.ProductionLogRepository;

@RestController
@RequestMapping("/api/production-logs")
public class ProductionLogApiController {
    @Autowired
    private ProductionLogRepository productionLogRepository;

    @GetMapping("/{productionId}")
    public List<ProductionLogEntity> getAllProductionLog(@PathVariable Long productionId) {
        return productionLogRepository.findAllByProductionIdOrderByIdDesc(productionId);
    }

    @PostMapping
    public ProductionLogEntity createProductionLog(@RequestBody ProductionLogEntity productionLog) {
        validate(productionLog);
        // Stamped here, not taken from the request: this is the audit trail of when the
        // entry was really keyed in, which a back-dated productionDate must not overwrite
        productionLog.setRecordedAt(LocalDateTime.now());
        return productionLogRepository.save(productionLog);
    }

    @PutMapping("/{id}")
    public ProductionLogEntity updateProductionLog(
            @PathVariable Long id,
            @RequestBody ProductionLogEntity productionLog) {
        validate(productionLog);

        ProductionLogEntity p = productionLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductionLog not found with id " + id));
        p.setProduction(productionLog.getProduction());
        p.setProductionDate(productionLog.getProductionDate());
        p.setRemark(productionLog.getRemark());
        p.setQty(productionLog.getQty());
        // recordedAt is deliberately left alone - it records the original entry

        return productionLogRepository.save(p);
    }

    private void validate(ProductionLogEntity productionLog) {
        if (productionLog.getQty() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (productionLog.getProductionDate() == null) {
            throw new IllegalArgumentException("Production date is required");
        }
        // Output cannot be recorded for a day that has not happened yet
        if (productionLog.getProductionDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Production date cannot be in the future");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteProductionLog(@PathVariable Long id) {
        productionLogRepository.deleteById(id);
    }
}

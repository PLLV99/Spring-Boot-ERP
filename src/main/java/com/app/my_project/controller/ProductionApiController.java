package com.app.my_project.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Sort;

import com.app.my_project.annotation.RequireRole;
import com.app.my_project.entity.ProductionEntity;
import com.app.my_project.repository.ProductionRepository;

@RestController
@RequestMapping("/api/productions")
public class ProductionApiController {
    private final ProductionRepository productionRepository;

    // Constructor injection for the ProductionRepository
    public ProductionApiController(ProductionRepository productionRepository) {
        this.productionRepository = productionRepository;
    }

    // CRUD operations for ProductionEntity
    @GetMapping
    public List<ProductionEntity> getAllProductions() {
        // Sort explicitly: findAll() has no ORDER BY, so PostgreSQL returns rows in
        // physical scan order - and an UPDATE rewrites the row at the end of the heap,
        // which makes edited products jump around in the list
        return productionRepository.findAll(Sort.by("id"));
    }

    @GetMapping("/{id}")
    public ProductionEntity getProduction(@PathVariable Long id) {
        return productionRepository.findById(id).orElse(null);
    }

    @PostMapping
    public ProductionEntity createProduction(@RequestBody ProductionEntity production) {
        return productionRepository.save(production);
    }

    @PutMapping("/{id}")
    public ProductionEntity updateProduction(@PathVariable Long id, @RequestBody ProductionEntity production) {
        ProductionEntity productionToUpdate = productionRepository.findById(id).orElse(null);

        if (productionToUpdate == null) {
            throw new IllegalArgumentException("Production not found");
        }

        productionToUpdate.setName(production.getName());
        productionToUpdate.setDetail(production.getDetail());
        productionToUpdate.setPrice(production.getPrice());

        return productionRepository.save(productionToUpdate);
    }

    @DeleteMapping("/{id}")
    public void deleteProduction(@PathVariable Long id) {
        productionRepository.deleteById(id);
    }

    // Pricing lives on the admin-only Accounting screen; the rest of this
    // controller stays open to employees, who work the Production screen
    @RequireRole("admin")
    @PutMapping("/updatePrice/{id}")
    public void updatePrice(
            @PathVariable Long id,
            @RequestBody ProductionEntity productionEntity) {
        ProductionEntity p = productionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("production not found"));
        p.setPrice(productionEntity.getPrice());
        productionRepository.save(p);
    }

}
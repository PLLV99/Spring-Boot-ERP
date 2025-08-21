package com.app.my_project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.my_project.entity.MaterialEntity;
import com.app.my_project.repository.MaterialRepository;

/**
 * MaterialApiController
 * This class is a REST API controller for managing materials.
 * It lets you create, read, update, and delete material data.
 */
@RestController
@RequestMapping("/api/materials") // All URLs in this controller start with /api/materials
public class MaterialApiController {
    // This is used to access the database for materials
    private final MaterialRepository materialRepository;

    // Constructor: Spring will give us a MaterialRepository to use
    public MaterialApiController(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    /**
     * Create a new material (POST /api/materials)
     * This method receives material data from the request body and saves it to the
     * database.
     * It returns the saved material.
     */
    @PostMapping
    public ResponseEntity<MaterialEntity> creaEntity(
            @RequestBody MaterialEntity materialEntity) {
        return ResponseEntity.ok(materialRepository.save(materialEntity));
    }

    // ดึงรายการวัสดุทั้งหมด (GET /api/materials)
    // Get all materials (GET /api/materials)
    // เมธอดนี้จะคืนค่ารายการวัสดุทั้งหมดจากฐานข้อมูล โดยเรียงจาก id มากไปน้อย
    // This method returns a list of all materials from the database, ordered by id
    // descending.
    @GetMapping
    public ResponseEntity<List<MaterialEntity>> getMethodName() {
        List<MaterialEntity> materials = materialRepository.findAllByOrderByIdDesc();
        return ResponseEntity.ok(materials);
    }

    /**
     * Update a material (PUT /api/materials/{id})
     * This method updates an existing material by id.
     * If the material is not found, it throws an error.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MaterialEntity> updateMaterial(
            @PathVariable Long id,
            @RequestBody MaterialEntity materialEntity) {
        MaterialEntity materialForUpdate = materialRepository.findById(id).orElse(null);

        if (materialForUpdate == null) {
            throw new IllegalArgumentException("Material not found");
        }

        materialForUpdate.setName(materialEntity.getName());
        materialForUpdate.setUnitName(materialEntity.getUnitName());
        materialForUpdate.setQty(materialEntity.getQty());

        return ResponseEntity.ok(materialRepository.save(materialForUpdate));
    }

    /**
     * Delete a material (DELETE /api/materials/{id})
     * This method deletes a material by id.
     */
    @DeleteMapping("/{id}")
    public void deleteMaterial(@PathVariable Long id) {
        materialRepository.deleteById(id);
    }
}
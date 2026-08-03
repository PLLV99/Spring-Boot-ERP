package com.app.my_project.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.app.my_project.repository.FormulaRepository;
import com.app.my_project.entity.FormulaEntity;

import java.util.List;

@RestController
@RequestMapping("/api/formulas")
public class FormulaApiController {
    @Autowired
    private final FormulaRepository formulaRepository;

    public FormulaApiController(FormulaRepository formulaRepository) {
        this.formulaRepository = formulaRepository;
    }

    @GetMapping("/{productId}")
    public List<FormulaEntity> getAllFormulas(@PathVariable Long productId) {
        return formulaRepository.findAllByProductionIdOrderByIdDesc(productId);
    }

    @PostMapping
    public FormulaEntity createFormula(@RequestBody FormulaEntity formulaEntity) {
        requirePositiveQty(formulaEntity);
        return formulaRepository.save(formulaEntity);
    }

    // A recipe line with no quantity, or a negative one, is meaningless - reject it
    // here rather than storing it and having the shortage calculation go wrong later
    private void requirePositiveQty(FormulaEntity formulaEntity) {
        if (formulaEntity.getQty() == null || formulaEntity.getQty().signum() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    @PutMapping("/{id}")
    public FormulaEntity updateFormula(
            @PathVariable Long id,
            @RequestBody FormulaEntity formulaEntity) {
        requirePositiveQty(formulaEntity);

        FormulaEntity formula = formulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formula not found"));

        formula.setQty(formulaEntity.getQty());
        formula.setUnit(formulaEntity.getUnit());
        formula.setMaterial(formulaEntity.getMaterial());
        formula.setProduction(formulaEntity.getProduction());

        return formulaRepository.save(formula);
    }

    @DeleteMapping("/{id}")
    public void deleteFormula(@PathVariable Long id) {
        formulaRepository.deleteById(id);
    }
}

package com.app.my_project.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class FormulaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // BigDecimal, not int: a recipe legitimately needs 0.3 kg of a material, and an
    // int column silently truncated that to 0 - the request still answered 200 while
    // the saved recipe said "use none of this". NUMERIC(12,4) rather than double so
    // quantities add up exactly when requirements are rolled up across a batch.
    @Column(precision = 12, scale = 4)
    private BigDecimal qty;

    // Unit of measure, carried over from the material this line refers to
    private String unit;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private MaterialEntity material;

    @ManyToOne
    @JoinColumn(name = "production_id")
    private ProductionEntity production;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public MaterialEntity getMaterial() {
        return material;
    }

    public void setMaterial(MaterialEntity material) {
        this.material = material;
    }

    public ProductionEntity getProduction() {
        return production;
    }

    public void setProduction(ProductionEntity production) {
        this.production = production;
    }

}

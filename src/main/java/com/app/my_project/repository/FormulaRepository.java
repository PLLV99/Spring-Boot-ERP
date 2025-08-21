package com.app.my_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.my_project.entity.FormulaEntity;

public interface FormulaRepository extends JpaRepository<FormulaEntity, Long> {
    List<FormulaEntity> findAllByProductionIdOrderByIdDesc(Long productId);
}

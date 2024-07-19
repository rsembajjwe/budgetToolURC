package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.StockUnitMeasure;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockUnitMeasureRepository extends JpaRepository<StockUnitMeasure, Long> {

    List<StockUnitMeasure> findByCode(String code);

    List<StockUnitMeasure> findByUnit(String code);
    // Custom query to search by code or unit using LIKE

    @Query("SELECT su FROM StockUnitMeasure su WHERE su.code LIKE %:keyword% OR su.unit LIKE %:keyword%")
    List<StockUnitMeasure> findByCodeOrUnitLike(String keyword);

    @Query("SELECT DISTINCT su.unit FROM StockUnitMeasure su")
    List<String> findAllUnitValues();
}

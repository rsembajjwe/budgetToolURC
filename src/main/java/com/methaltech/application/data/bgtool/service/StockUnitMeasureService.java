package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.StockUnitMeasure;
import com.methaltech.application.data.bgtool.repository.StockUnitMeasureRepository;
import com.methaltech.application.data.entity.bgtool.StockUnitPojo;
import com.methaltech.application.data.bgtool.repository.StockUnitRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StockUnitMeasureService {

    private final StockUnitMeasureRepository repository;
    private final StockUnitRepository stockUnitRepository;

    @Autowired
    public StockUnitMeasureService(StockUnitMeasureRepository repository, StockUnitRepository stockUnitRepository) {
        this.repository = repository;
        this.stockUnitRepository = stockUnitRepository;
    }

    public Optional<StockUnitMeasure> get(Long id) {
        return repository.findById(id);
    }

    public StockUnitMeasure update(StockUnitMeasure entity) {
        return repository.save(entity);
    }

    public Page<StockUnitMeasure> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<StockUnitMeasure> getStockUnitMeasureByCode(String code) {
        return repository.findByCode(code);
    }

    public List<StockUnitMeasure> getStockUnitMeasureByUnit(String code) {
        return repository.findByUnit(code);
    }

    public void updateTransfer() {
        List<StockUnitPojo> findall = stockUnitRepository.findAll();
        for (StockUnitPojo a : findall) {
            StockUnitMeasure ab = new StockUnitMeasure();
            ab.setCode(a.getCode());
            ab.setUnit(a.getUnit());
            repository.save(ab);
        }

    }

    public Long count() {
        return repository.count();
    }

    public List<StockUnitMeasure> search(String search) {
        return repository.findByCodeOrUnitLike(search);
    }

    public List<String> findAllUnitValues() {
        return repository.findAllUnitValues();
    }
}

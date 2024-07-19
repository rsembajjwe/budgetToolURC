
package com.methaltech.application.data.oldbgtool.service;
import com.methaltech.application.data.entity.oldbgtool.StockUnitPojo;
import com.methaltech.application.data.oldbgtool.repository.StockUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockUnitService {

    private final StockUnitRepository stockUnitRepository;

    @Autowired
    public StockUnitService(StockUnitRepository stockUnitRepository) {
        this.stockUnitRepository = stockUnitRepository;
    }

    public List<StockUnitPojo> getAllStockUnits() {
        return stockUnitRepository.findAll();
    }

    public Optional<StockUnitPojo> getStockUnitById(int id) {
        return stockUnitRepository.findById(id);
    }

    public StockUnitPojo createStockUnit(StockUnitPojo stockUnit) {
        return stockUnitRepository.save(stockUnit);
    }

    public StockUnitPojo updateStockUnit(StockUnitPojo stockUnit) {
        return stockUnitRepository.save(stockUnit);
    }

    public void deleteStockUnit(int id) {
        stockUnitRepository.deleteById(id);
    }
    public Long count(){
        return stockUnitRepository.count();
    }
}


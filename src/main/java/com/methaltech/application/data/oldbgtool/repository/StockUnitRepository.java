
package com.methaltech.application.data.oldbgtool.repository;
import com.methaltech.application.data.entity.oldbgtool.StockUnitPojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockUnitRepository extends JpaRepository<StockUnitPojo, Integer> {
    // You can add custom query methods here if needed
}

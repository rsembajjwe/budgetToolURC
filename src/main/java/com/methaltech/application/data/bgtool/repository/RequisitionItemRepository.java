
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.RequisitionData;
import com.methaltech.application.data.entity.bgtool.RequisitionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionItemRepository extends JpaRepository<RequisitionItem, Long> {

    // Find by requisition data
    List<RequisitionItem> findByRequisitionDataOrderByItemNumberAsc(RequisitionData requisitionData);
    
    // Find by requisition data ID
    @Query("SELECT ri FROM RequisitionItem ri WHERE ri.requisitionData.id = :requisitionDataId ORDER BY ri.itemNumber ASC")
    List<RequisitionItem> findByRequisitionDataIdOrderByItemNumberAsc(@Param("requisitionDataId") Long requisitionDataId);
    
    // Count items by requisition
    Long countByRequisitionData(RequisitionData requisitionData);
    
    // Sum total cost by requisition
    @Query("SELECT SUM(ri.totalCost) FROM RequisitionItem ri WHERE ri.requisitionData = :requisitionData")
    Double sumTotalCostByRequisitionData(@Param("requisitionData") RequisitionData requisitionData);
    
    // Find items by description containing
    @Query("SELECT ri FROM RequisitionItem ri WHERE LOWER(ri.description) LIKE LOWER(CONCAT('%', :description, '%')) ORDER BY ri.createdDate DESC")
    List<RequisitionItem> findByDescriptionContaining(@Param("description") String description);
    
    // Find items by unit of measure
    List<RequisitionItem> findByUnitOfMeasureOrderByCreatedDateDesc(String unitOfMeasure);
    
    // Find items by cost range
    @Query("SELECT ri FROM RequisitionItem ri WHERE ri.estimatedUnitCost BETWEEN :minCost AND :maxCost ORDER BY ri.estimatedUnitCost ASC")
    List<RequisitionItem> findByCostRange(@Param("minCost") Double minCost, @Param("maxCost") Double maxCost);
}

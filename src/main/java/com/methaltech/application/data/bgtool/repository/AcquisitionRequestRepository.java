
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.AcquisitionRequest;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Organisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AcquisitionRequestRepository extends JpaRepository<AcquisitionRequest, Long> {

    // Find by acquisition number
    Optional<AcquisitionRequest> findByAcquisitionNumber(String acquisitionNumber);
    
    // Find by budget
    @Query("SELECT ar FROM AcquisitionRequest ar LEFT JOIN FETCH ar.budget LEFT JOIN FETCH ar.organisation LEFT JOIN FETCH ar.coa WHERE ar.budget = :budget ORDER BY ar.createdDate DESC")
    List<AcquisitionRequest> findByBudgetOrderByCreatedDateDesc(@Param("budget") Budget budget);
    
    // Find by organisation
    @Query("SELECT ar FROM AcquisitionRequest ar LEFT JOIN FETCH ar.budget LEFT JOIN FETCH ar.organisation LEFT JOIN FETCH ar.coa WHERE ar.organisation = :organisation ORDER BY ar.createdDate DESC")
    List<AcquisitionRequest> findByOrganisationOrderByCreatedDateDesc(@Param("organisation") Organisation organisation);
    
    // Find by COA
    @Query("SELECT ar FROM AcquisitionRequest ar LEFT JOIN FETCH ar.budget LEFT JOIN FETCH ar.organisation LEFT JOIN FETCH ar.coa WHERE ar.coa = :coa ORDER BY ar.createdDate DESC")
    List<AcquisitionRequest> findByCoaOrderByCreatedDateDesc(@Param("coa") COA coa);
    
    // Find by creator
    @Query("SELECT ar FROM AcquisitionRequest ar LEFT JOIN FETCH ar.budget LEFT JOIN FETCH ar.organisation LEFT JOIN FETCH ar.coa WHERE ar.createdBy = :createdBy ORDER BY ar.createdDate DESC")
    List<AcquisitionRequest> findByCreatedByOrderByCreatedDateDesc(@Param("createdBy") String createdBy);
    
    // Find by status
    @Query("SELECT ar FROM AcquisitionRequest ar LEFT JOIN FETCH ar.budget LEFT JOIN FETCH ar.organisation LEFT JOIN FETCH ar.coa WHERE ar.status = :status ORDER BY ar.createdDate DESC")
    List<AcquisitionRequest> findByStatusOrderByCreatedDateDesc(@Param("status") AcquisitionRequest.AcquisitionStatus status);
    
    // Find by status with priority ordering
    @Query("SELECT ar FROM AcquisitionRequest ar LEFT JOIN FETCH ar.budget LEFT JOIN FETCH ar.organisation LEFT JOIN FETCH ar.coa WHERE ar.status = :status ORDER BY ar.priorityLevel DESC, ar.createdDate ASC")
    List<AcquisitionRequest> findByStatusOrderByPriorityLevelDescCreatedDateAsc(@Param("status") AcquisitionRequest.AcquisitionStatus status);
    
    // Find high priority acquisitions
    @Query("SELECT ar FROM AcquisitionRequest ar LEFT JOIN FETCH ar.budget LEFT JOIN FETCH ar.organisation LEFT JOIN FETCH ar.coa WHERE ar.priorityLevel IN ('HIGH', 'URGENT', 'EMERGENCY') AND ar.status = 'SUBMITTED' ORDER BY ar.priorityLevel DESC, ar.createdDate ASC")
    List<AcquisitionRequest> findHighPriorityAcquisitions();
    
    // Search acquisitions
    @Query("SELECT ar FROM AcquisitionRequest ar LEFT JOIN FETCH ar.budget LEFT JOIN FETCH ar.organisation LEFT JOIN FETCH ar.coa WHERE " +
           "LOWER(ar.acquisitionNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(ar.purpose) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(ar.vendorName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY ar.createdDate DESC")
    Page<AcquisitionRequest> searchAcquisitions(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Count by status
    Long countByStatus(AcquisitionRequest.AcquisitionStatus status);
    
    // Sum amounts
    @Query("SELECT SUM(ar.requestedAmount) FROM AcquisitionRequest ar")
    Double sumTotalRequestedAmount();
    
    @Query("SELECT SUM(ar.requestedAmount) FROM AcquisitionRequest ar WHERE ar.status = 'APPROVED'")
    Double sumApprovedAmount();
}

package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetRequisition;
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
public interface BudgetRequisitionRepository extends JpaRepository<BudgetRequisition, Long> {

    // Find by requisition number
    Optional<BudgetRequisition> findByRequisitionNumber(String requisitionNumber);

    // Find by budget
    List<BudgetRequisition> findByBudgetOrderByCreatedDateDesc(Budget budget);

    // Find by department
    List<BudgetRequisition> findByDepartmentCodeOrderByCreatedDateDesc(String departmentCode);

    // Find by section
    List<BudgetRequisition> findByDepartmentCodeAndSectionCodeOrderByCreatedDateDesc(String departmentCode, String sectionCode);

    // Find by account code
    List<BudgetRequisition> findByDepartmentCodeAndSectionCodeAndAccountCodeOrderByCreatedDateDesc(
            String departmentCode, String sectionCode, String accountCode);

    // Find by creator
    List<BudgetRequisition> findByCreatedByOrderByCreatedDateDesc(String createdBy);

    // Find by status
    List<BudgetRequisition> findByStatusOrderByCreatedDateDesc(BudgetRequisition.RequisitionStatus status);

    // Find by status with priority ordering
    @Query("SELECT br FROM BudgetRequisition br WHERE br.status = :status ORDER BY br.priorityLevel DESC, br.createdDate ASC")
    List<BudgetRequisition> findByStatusOrderByPriorityLevelDescCreatedDateAsc(@Param("status") BudgetRequisition.RequisitionStatus status);

    // Find by approval status
    List<BudgetRequisition> findByApprovalStatusOrderByCreatedDateDesc(BudgetRequisition.ApprovalStatus approvalStatus);

    // Find by requisition type
    List<BudgetRequisition> findByRequisitionTypeOrderByCreatedDateDesc(BudgetRequisition.RequisitionType requisitionType);

    // Find by priority level
    List<BudgetRequisition> findByPriorityLevelOrderByCreatedDateDesc(BudgetRequisition.PriorityLevel priorityLevel);

    // Find high priority requisitions
    @Query("SELECT br FROM BudgetRequisition br WHERE br.priorityLevel IN ('HIGH', 'URGENT', 'EMERGENCY') AND br.status = 'SUBMITTED' ORDER BY br.priorityLevel DESC, br.createdDate ASC")
    List<BudgetRequisition> findHighPriorityRequisitions();

    // Find over budget requisitions
    @Query("SELECT br FROM BudgetRequisition br WHERE br.requestedAmount > br.availableBudget ORDER BY br.createdDate DESC")
    List<BudgetRequisition> findOverBudgetRequisitions();

    // Find requisitions within date range
    @Query("SELECT br FROM BudgetRequisition br WHERE br.createdDate BETWEEN :startDate AND :endDate ORDER BY br.createdDate DESC")
    List<BudgetRequisition> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Find requisitions by amount range
    @Query("SELECT br FROM BudgetRequisition br WHERE br.requestedAmount BETWEEN :minAmount AND :maxAmount ORDER BY br.requestedAmount DESC")
    List<BudgetRequisition> findByAmountRange(@Param("minAmount") Double minAmount, @Param("maxAmount") Double maxAmount);

    // Search requisitions
    @Query("SELECT br FROM BudgetRequisition br WHERE "
            + "LOWER(br.requisitionNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(br.departmentName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(br.sectionName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(br.accountName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(br.purpose) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(br.vendorName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
            + "ORDER BY br.createdDate DESC")
    Page<BudgetRequisition> searchRequisitions(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Count by status
    Long countByStatus(BudgetRequisition.RequisitionStatus status);

    // Count by department
    Long countByDepartmentCode(String departmentCode);

    // Count by section
    Long countByDepartmentCodeAndSectionCode(String departmentCode, String sectionCode);

    // Count by approval status
    Long countByApprovalStatus(BudgetRequisition.ApprovalStatus approvalStatus);

    // Sum total requested amount
    @Query("SELECT SUM(br.requestedAmount) FROM BudgetRequisition br")
    Double sumTotalRequestedAmount();

    // Sum approved amount
    @Query("SELECT SUM(br.requestedAmount) FROM BudgetRequisition br WHERE br.status = 'APPROVED'")
    Double sumApprovedAmount();

    // Sum requested amount by department
    @Query("SELECT SUM(br.requestedAmount) FROM BudgetRequisition br WHERE br.departmentCode = :departmentCode")
    Double sumRequestedAmountByDepartment(@Param("departmentCode") String departmentCode);

    // Sum requested amount by section
    @Query("SELECT SUM(br.requestedAmount) FROM BudgetRequisition br WHERE br.departmentCode = :departmentCode AND br.sectionCode = :sectionCode")
    Double sumRequestedAmountBySection(@Param("departmentCode") String departmentCode, @Param("sectionCode") String sectionCode);

    // Sum requested amount by status
    @Query("SELECT SUM(br.requestedAmount) FROM BudgetRequisition br WHERE br.status = :status")
    Double sumRequestedAmountByStatus(@Param("status") BudgetRequisition.RequisitionStatus status);

    // Count requisitions by type
    @Query("SELECT br.requisitionType, COUNT(br) FROM BudgetRequisition br GROUP BY br.requisitionType")
    List<Object[]> countRequisitionsByType();

    // Count requisitions by department
    @Query("SELECT br.departmentCode, COUNT(br) FROM BudgetRequisition br GROUP BY br.departmentCode")
    List<Object[]> countRequisitionsByDepartment();

    // Find recent requisitions
    @Query("SELECT br FROM BudgetRequisition br WHERE br.createdDate >= :since ORDER BY br.createdDate DESC")
    List<BudgetRequisition> findRecentRequisitions(@Param("since") LocalDateTime since);

    // Find requisitions requiring attention (high priority or overdue)
    @Query("SELECT br FROM BudgetRequisition br WHERE "
            + "(br.priorityLevel IN ('HIGH', 'URGENT', 'EMERGENCY') AND br.status = 'SUBMITTED') OR "
            + "(br.status = 'SUBMITTED' AND br.createdDate < :overdueDate) "
            + "ORDER BY br.priorityLevel DESC, br.createdDate ASC")
    List<BudgetRequisition> findRequisitionsRequiringAttention(@Param("overdueDate") LocalDateTime overdueDate);
}

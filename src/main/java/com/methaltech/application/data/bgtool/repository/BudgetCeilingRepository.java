
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetCeiling;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BudgetCeilingRepository extends JpaRepository<BudgetCeiling, Long> {

    // Find by budget
    List<BudgetCeiling> findByBudgetOrderByCreatedDateDesc(Budget budget);
    
    // Find by budget and ceiling type
    List<BudgetCeiling> findByBudgetAndCeilingTypeOrderByCreatedDateDesc(Budget budget, BudgetCeiling.CeilingType ceilingType);
    
    // Find by budget and department
    List<BudgetCeiling> findByBudgetAndDepartmentCodeOrderByCreatedDateDesc(Budget budget, String departmentCode);
    
    // Find by budget and section
    List<BudgetCeiling> findByBudgetAndSectionCodeOrderByCreatedDateDesc(Budget budget, String sectionCode);
    
    // Find by budget and revenue source
    List<BudgetCeiling> findByBudgetAndRevenueSourceCodeOrderByCreatedDateDesc(Budget budget, String revenueSourceCode);
    
    // Find by budget and account code
    List<BudgetCeiling> findByBudgetAndAccountCodeOrderByCreatedDateDesc(Budget budget, String accountCode);
    
    // Find by status
    List<BudgetCeiling> findByBudgetAndCeilingStatusOrderByCreatedDateDesc(Budget budget, BudgetCeiling.CeilingStatus status);
    
    // Find by approval status
    List<BudgetCeiling> findByBudgetAndApprovalStatusOrderByCreatedDateDesc(Budget budget, BudgetCeiling.ApprovalStatus approvalStatus);
    
    // Find active ceilings
    @Query("SELECT bc FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.ceilingStatus = 'ACTIVE' AND bc.approvalStatus = 'APPROVED' AND bc.effectiveDate <= :now AND (bc.expiryDate IS NULL OR bc.expiryDate > :now) ORDER BY bc.createdDate DESC")
    List<BudgetCeiling> findActiveCeilings(@Param("budget") Budget budget, @Param("now") LocalDateTime now);
    
    // Find over ceiling items
    @Query("SELECT bc FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.ceilingStatus = 'ACTIVE' AND (bc.spentAmount + bc.committedAmount) > bc.ceilingAmount ORDER BY ((bc.spentAmount + bc.committedAmount) / bc.ceilingAmount) DESC")
    List<BudgetCeiling> findOverCeilingItems(@Param("budget") Budget budget);
    
    // Find near ceiling items (80-95% utilization)
    @Query("SELECT bc FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.ceilingStatus = 'ACTIVE' AND ((bc.spentAmount + bc.committedAmount) / bc.ceilingAmount) BETWEEN 0.8 AND 0.95 ORDER BY ((bc.spentAmount + bc.committedAmount) / bc.ceilingAmount) DESC")
    List<BudgetCeiling> findNearCeilingItems(@Param("budget") Budget budget);
    
    // Find critical ceiling items (95%+ utilization)
    @Query("SELECT bc FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.ceilingStatus = 'ACTIVE' AND ((bc.spentAmount + bc.committedAmount) / bc.ceilingAmount) > 0.95 ORDER BY ((bc.spentAmount + bc.committedAmount) / bc.ceilingAmount) DESC")
    List<BudgetCeiling> findCriticalCeilingItems(@Param("budget") Budget budget);
    
    // Find expiring ceilings
    @Query("SELECT bc FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.ceilingStatus = 'ACTIVE' AND bc.expiryDate BETWEEN :startDate AND :endDate ORDER BY bc.expiryDate ASC")
    List<BudgetCeiling> findExpiringCeilings(@Param("budget") Budget budget, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Search ceilings
    @Query("SELECT bc FROM BudgetCeiling bc WHERE bc.budget = :budget AND " +
           "(LOWER(bc.departmentName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(bc.sectionName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(bc.revenueSourceName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(bc.accountName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(bc.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY bc.createdDate DESC")
    Page<BudgetCeiling> searchCeilings(@Param("budget") Budget budget, @Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT bc FROM BudgetCeiling bc WHERE " +
           "(LOWER(bc.departmentName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(bc.sectionName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(bc.revenueSourceName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(bc.accountName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(bc.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY bc.createdDate DESC")
    Page<BudgetCeiling> searchCeilings(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Dashboard statistics
    @Query("SELECT COUNT(bc) FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.ceilingStatus = 'ACTIVE' AND bc.approvalStatus = 'APPROVED'")
    Long countActiveCeilings(@Param("budget") Budget budget);
    
    @Query("SELECT COUNT(bc) FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.ceilingStatus = 'ACTIVE' AND (bc.spentAmount + bc.committedAmount) > bc.ceilingAmount")
    Long countOverCeilingItems(@Param("budget") Budget budget);
    
    @Query("SELECT COUNT(bc) FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.ceilingStatus = 'ACTIVE' AND ((bc.spentAmount + bc.committedAmount) / bc.ceilingAmount) BETWEEN 0.8 AND 0.95")
    Long countNearCeilingItems(@Param("budget") Budget budget);
    
    @Query("SELECT COUNT(bc) FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.ceilingStatus = 'ACTIVE' AND ((bc.spentAmount + bc.committedAmount) / bc.ceilingAmount) > 0.95")
    Long countCriticalCeilingItems(@Param("budget") Budget budget);
    
    @Query("SELECT SUM(bc.ceilingAmount) FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.ceilingStatus = 'ACTIVE'")
    Double sumTotalCeilingAmount(@Param("budget") Budget budget);
    
    @Query("SELECT SUM(bc.allocatedAmount) FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.ceilingStatus = 'ACTIVE'")
    Double sumTotalAllocatedAmount(@Param("budget") Budget budget);
    
    @Query("SELECT SUM(bc.spentAmount) FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.ceilingStatus = 'ACTIVE'")
    Double sumTotalSpentAmount(@Param("budget") Budget budget);
    
    @Query("SELECT SUM(bc.committedAmount) FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.ceilingStatus = 'ACTIVE'")
    Double sumTotalCommittedAmount(@Param("budget") Budget budget);
    
    // Find by hierarchy
    @Query("SELECT bc FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.departmentCode = :departmentCode AND bc.sectionCode = :sectionCode AND bc.ceilingType = 'SECTION'")
    List<BudgetCeiling> findSectionCeiling(@Param("budget") Budget budget, @Param("departmentCode") String departmentCode, @Param("sectionCode") String sectionCode);
    
    @Query("SELECT bc FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.departmentCode = :departmentCode AND bc.sectionCode = :sectionCode AND bc.revenueSourceCode = :revenueSourceCode AND bc.ceilingType = 'REVENUE_SOURCE'")
    List<BudgetCeiling> findRevenueSourceCeiling(@Param("budget") Budget budget, @Param("departmentCode") String departmentCode, @Param("sectionCode") String sectionCode, @Param("revenueSourceCode") String revenueSourceCode);
    
    @Query("SELECT bc FROM BudgetCeiling bc WHERE bc.budget = :budget AND bc.departmentCode = :departmentCode AND bc.sectionCode = :sectionCode AND bc.revenueSourceCode = :revenueSourceCode AND bc.accountCode = :accountCode AND bc.ceilingType = 'ACCOUNT_CODE'")
    List<BudgetCeiling> findAccountCodeCeiling(@Param("budget") Budget budget, @Param("departmentCode") String departmentCode, @Param("sectionCode") String sectionCode, @Param("revenueSourceCode") String revenueSourceCode, @Param("accountCode") String accountCode);
}

package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetApprovals;
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
public interface BudgetApprovalsRepository extends JpaRepository<BudgetApprovals, Long> {

    // Find by request ID
    Optional<BudgetApprovals> findByRequestId(String requestId);
    
    // Find by department
    List<BudgetApprovals> findByDepartmentCodeOrderByRequestedDateDesc(String departmentCode);
    
    // Find by current stage
    List<BudgetApprovals> findByCurrentStageOrderByRequestedDateDesc(BudgetApprovals.ApprovalStage currentStage);
    
    // Find by overall status
    List<BudgetApprovals> findByOverallStatusOrderByRequestedDateDesc(BudgetApprovals.ApprovalStatus overallStatus);
    
    // Find by priority level
    List<BudgetApprovals> findByPriorityLevelOrderByRequestedDateDesc(BudgetApprovals.PriorityLevel priorityLevel);
    
    // Find by requester
    List<BudgetApprovals> findByRequestedByOrderByRequestedDateDesc(String requestedBy);
    
    // Find by budget
    List<BudgetApprovals> findByBudgetOrderByRequestedDateDesc(Budget budget);
    
    // Find by budget ID
    List<BudgetApprovals> findByBudget_IdOrderByRequestedDateDesc(Long budgetId);
    
    // Find by budget and status
    @Query("SELECT ba FROM BudgetApprovals ba WHERE ba.budget = :budget AND ba.overallStatus = :status ORDER BY ba.requestedDate DESC")
    List<BudgetApprovals> findByBudgetAndStatus(@Param("budget") Budget budget, 
                                              @Param("status") BudgetApprovals.ApprovalStatus status);
    
    // Find by budget and department
    @Query("SELECT ba FROM BudgetApprovals ba WHERE ba.budget = :budget AND ba.departmentCode = :departmentCode ORDER BY ba.requestedDate DESC")
    List<BudgetApprovals> findByBudgetAndDepartment(@Param("budget") Budget budget, 
                                                  @Param("departmentCode") String departmentCode);
    
    // Find pending approvals for specific role
    @Query("SELECT ba FROM BudgetApprovals ba WHERE ba.currentStage = :stage AND ba.overallStatus = 'IN_PROGRESS' ORDER BY ba.priorityLevel DESC, ba.requestedDate ASC")
    List<BudgetApprovals> findPendingApprovalsByStage(@Param("stage") BudgetApprovals.ApprovalStage stage);
    
    // Find approvals by department and status
    @Query("SELECT ba FROM BudgetApprovals ba WHERE ba.departmentCode = :departmentCode AND ba.overallStatus = :status ORDER BY ba.requestedDate DESC")
    List<BudgetApprovals> findByDepartmentAndStatus(@Param("departmentCode") String departmentCode, 
                                                  @Param("status") BudgetApprovals.ApprovalStatus status);
    
    // Find approvals within date range
    @Query("SELECT ba FROM BudgetApprovals ba WHERE ba.requestedDate BETWEEN :startDate AND :endDate ORDER BY ba.requestedDate DESC")
    List<BudgetApprovals> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    // Find high priority pending approvals
    @Query("SELECT ba FROM BudgetApprovals ba WHERE ba.priorityLevel IN ('HIGH', 'URGENT') AND ba.overallStatus = 'IN_PROGRESS' ORDER BY ba.priorityLevel DESC, ba.requestedDate ASC")
    List<BudgetApprovals> findHighPriorityPendingApprovals();
    
    // Count approvals by stage
    @Query("SELECT ba.currentStage, COUNT(ba) FROM BudgetApprovals ba WHERE ba.overallStatus = 'IN_PROGRESS' GROUP BY ba.currentStage")
    List<Object[]> countApprovalsByStage();
    
    // Count approvals by department
    @Query("SELECT ba.departmentCode, COUNT(ba) FROM BudgetApprovals ba WHERE ba.overallStatus = 'IN_PROGRESS' GROUP BY ba.departmentCode")
    List<Object[]> countApprovalsByDepartment();
    
    // Find overdue approvals (more than 7 days in current stage)
    @Query("SELECT ba FROM BudgetApprovals ba WHERE ba.overallStatus = 'IN_PROGRESS' AND " +
           "((ba.currentStage = 'BLO_REVIEW' AND ba.requestedDate < :sevenDaysAgo) OR " +
           "(ba.currentStage = 'HOD_REVIEW' AND ba.bloApprovedDate < :sevenDaysAgo) OR " +
           "(ba.currentStage = 'ADMIN_REVIEW' AND ba.hodApprovedDate < :sevenDaysAgo) OR " +
           "(ba.currentStage = 'CFO_REVIEW' AND ba.adminApprovedDate < :sevenDaysAgo)) " +
           "ORDER BY ba.priorityLevel DESC, ba.requestedDate ASC")
    List<BudgetApprovals> findOverdueApprovals(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);
    
    // Search approvals
    @Query("SELECT ba FROM BudgetApprovals ba WHERE " +
           "LOWER(ba.departmentName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(ba.sectionName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(ba.requestId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(ba.justification) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY ba.requestedDate DESC")
    Page<BudgetApprovals> searchApprovals(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Dashboard statistics
    @Query("SELECT COUNT(ba) FROM BudgetApprovals ba WHERE ba.overallStatus = 'IN_PROGRESS'")
    Long countPendingApprovals();
    
    @Query("SELECT COUNT(ba) FROM BudgetApprovals ba WHERE ba.overallStatus = 'APPROVED' AND ba.cfoApprovedDate >= :startDate")
    Long countApprovedThisMonth(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(ba) FROM BudgetApprovals ba WHERE ba.overallStatus = 'REJECTED' AND ba.rejectedDate >= :startDate")
    Long countRejectedThisMonth(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT SUM(ba.requestedAmount) FROM BudgetApprovals ba WHERE ba.overallStatus = 'APPROVED' AND ba.cfoApprovedDate >= :startDate")
    Double sumApprovedAmountThisMonth(@Param("startDate") LocalDateTime startDate);
}

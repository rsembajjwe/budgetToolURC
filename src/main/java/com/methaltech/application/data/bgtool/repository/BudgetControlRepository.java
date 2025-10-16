package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetControlRepository extends JpaRepository<BudgetControl, Long> {

    // Find by budget
    @Query("SELECT bc FROM BudgetControl bc LEFT JOIN FETCH bc.budget WHERE bc.budget = :budget ORDER BY bc.controlLevel ASC, bc.departmentCode ASC")
    List<BudgetControl> findByBudgetOrderByControlLevelAscDepartmentCodeAsc(@Param("budget") Budget budget);
    
    // Find by budget and control level
    @Query("SELECT bc FROM BudgetControl bc LEFT JOIN FETCH bc.budget WHERE bc.budget = :budget AND bc.controlLevel = :controlLevel ORDER BY bc.departmentCode ASC")
    List<BudgetControl> findByBudgetAndControlLevelOrderByDepartmentCodeAsc(@Param("budget") Budget budget, @Param("controlLevel") BudgetControl.ControlLevel controlLevel);
    
    // Find by budget and department
    @Query("SELECT bc FROM BudgetControl bc LEFT JOIN FETCH bc.budget WHERE bc.budget = :budget AND bc.departmentCode = :departmentCode ORDER BY bc.controlLevel ASC")
    List<BudgetControl> findByBudgetAndDepartmentCodeOrderByControlLevelAsc(@Param("budget") Budget budget, @Param("departmentCode") String departmentCode);
    
    // Find by budget, department and section
    @Query("SELECT bc FROM BudgetControl bc LEFT JOIN FETCH bc.budget WHERE bc.budget = :budget AND bc.departmentCode = :departmentCode AND bc.sectionCode = :sectionCode")
    Optional<BudgetControl> findByBudgetAndDepartmentCodeAndSectionCode(@Param("budget") Budget budget, @Param("departmentCode") String departmentCode, @Param("sectionCode") String sectionCode);
    
    // Find budget level control
    @Query("SELECT bc FROM BudgetControl bc LEFT JOIN FETCH bc.budget WHERE bc.budget = :budget AND bc.controlLevel = :controlLevel")
    Optional<BudgetControl> findByBudgetAndControlLevel(@Param("budget") Budget budget, @Param("controlLevel") BudgetControl.ControlLevel controlLevel);
    
    // Find department level control
    @Query("SELECT bc FROM BudgetControl bc LEFT JOIN FETCH bc.budget WHERE bc.budget = :budget AND bc.departmentCode = :departmentCode AND bc.controlLevel = :controlLevel")
    Optional<BudgetControl> findByBudgetAndDepartmentCodeAndControlLevel(@Param("budget") Budget budget, @Param("departmentCode") String departmentCode, @Param("controlLevel") BudgetControl.ControlLevel controlLevel);
    
    // Find active controls
    @Query("SELECT bc FROM BudgetControl bc LEFT JOIN FETCH bc.budget WHERE bc.budget = :budget AND bc.controlStatus = 'ACTIVE' AND bc.effectiveDate <= :now AND (bc.expiryDate IS NULL OR bc.expiryDate > :now) ORDER BY bc.controlLevel ASC, bc.departmentCode ASC")
    List<BudgetControl> findActiveControls(@Param("budget") Budget budget, @Param("now") LocalDateTime now);
    
    // Find controls with budget check enabled
    @Query("SELECT bc FROM BudgetControl bc LEFT JOIN FETCH bc.budget WHERE bc.budget = :budget AND bc.budgetCheckEnabled = true AND bc.controlStatus = 'ACTIVE' ORDER BY bc.controlLevel ASC")
    List<BudgetControl> findBudgetCheckEnabledControls(@Param("budget") Budget budget);
    
    // Find controls with budget stop enabled
    @Query("SELECT bc FROM BudgetControl bc LEFT JOIN FETCH bc.budget WHERE bc.budget = :budget AND bc.budgetStopEnabled = true AND bc.controlStatus = 'ACTIVE' ORDER BY bc.controlLevel ASC")
    List<BudgetControl> findBudgetStopEnabledControls(@Param("budget") Budget budget);
    
    // Find controls with posting prohibited
    @Query("SELECT bc FROM BudgetControl bc LEFT JOIN FETCH bc.budget WHERE bc.budget = :budget AND bc.postingProhibited = true AND bc.controlStatus = 'ACTIVE' ORDER BY bc.controlLevel ASC")
    List<BudgetControl> findPostingProhibitedControls(@Param("budget") Budget budget);
    
    // Check if budget check is enabled for specific hierarchy
    @Query("SELECT CASE WHEN COUNT(bc) > 0 THEN true ELSE false END FROM BudgetControl bc WHERE " +
           "bc.budget = :budget AND bc.budgetCheckEnabled = true AND bc.controlStatus = 'ACTIVE' AND " +
           "bc.effectiveDate <= :now AND (bc.expiryDate IS NULL OR bc.expiryDate > :now) AND " +
           "((bc.controlLevel = 'BUDGET') OR " +
           "(bc.controlLevel = 'DEPARTMENT' AND bc.departmentCode = :departmentCode) OR " +
           "(bc.controlLevel = 'SECTION' AND bc.departmentCode = :departmentCode AND bc.sectionCode = :sectionCode))")
    boolean isBudgetCheckEnabled(@Param("budget") Budget budget, 
                                @Param("departmentCode") String departmentCode, 
                                @Param("sectionCode") String sectionCode, 
                                @Param("now") LocalDateTime now);
    
    // Check if budget stop is enabled for specific hierarchy
    @Query("SELECT CASE WHEN COUNT(bc) > 0 THEN true ELSE false END FROM BudgetControl bc WHERE " +
           "bc.budget = :budget AND bc.budgetStopEnabled = true AND bc.controlStatus = 'ACTIVE' AND " +
           "bc.effectiveDate <= :now AND (bc.expiryDate IS NULL OR bc.expiryDate > :now) AND " +
           "((bc.controlLevel = 'BUDGET') OR " +
           "(bc.controlLevel = 'DEPARTMENT' AND bc.departmentCode = :departmentCode) OR " +
           "(bc.controlLevel = 'SECTION' AND bc.departmentCode = :departmentCode AND bc.sectionCode = :sectionCode))")
    boolean isBudgetStopEnabled(@Param("budget") Budget budget, 
                               @Param("departmentCode") String departmentCode, 
                               @Param("sectionCode") String sectionCode, 
                               @Param("now") LocalDateTime now);
    
    // Check if posting is prohibited for specific hierarchy
    @Query("SELECT CASE WHEN COUNT(bc) > 0 THEN true ELSE false END FROM BudgetControl bc WHERE " +
           "bc.budget = :budget AND bc.postingProhibited = true AND bc.controlStatus = 'ACTIVE' AND " +
           "bc.effectiveDate <= :now AND (bc.expiryDate IS NULL OR bc.expiryDate > :now) AND " +
           "((bc.controlLevel = 'BUDGET') OR " +
           "(bc.controlLevel = 'DEPARTMENT' AND bc.departmentCode = :departmentCode) OR " +
           "(bc.controlLevel = 'SECTION' AND bc.departmentCode = :departmentCode AND bc.sectionCode = :sectionCode))")
    boolean isPostingProhibited(@Param("budget") Budget budget, 
                               @Param("departmentCode") String departmentCode, 
                               @Param("sectionCode") String sectionCode, 
                               @Param("now") LocalDateTime now);
    
    // Find controls by status
    List<BudgetControl> findByBudgetAndControlStatusOrderByControlLevelAsc(Budget budget, BudgetControl.ControlStatus status);
    
    // Find expiring controls
    @Query("SELECT bc FROM BudgetControl bc WHERE bc.budget = :budget AND bc.controlStatus = 'ACTIVE' AND bc.expiryDate BETWEEN :startDate AND :endDate ORDER BY bc.expiryDate ASC")
    List<BudgetControl> findExpiringControls(@Param("budget") Budget budget, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Count controls by type
    @Query("SELECT COUNT(bc) FROM BudgetControl bc WHERE bc.budget = :budget AND bc.budgetCheckEnabled = true AND bc.controlStatus = 'ACTIVE'")
    Long countBudgetCheckControls(@Param("budget") Budget budget);
    
    @Query("SELECT COUNT(bc) FROM BudgetControl bc WHERE bc.budget = :budget AND bc.budgetStopEnabled = true AND bc.controlStatus = 'ACTIVE'")
    Long countBudgetStopControls(@Param("budget") Budget budget);
    
    @Query("SELECT COUNT(bc) FROM BudgetControl bc WHERE bc.budget = :budget AND bc.postingProhibited = true AND bc.controlStatus = 'ACTIVE'")
    Long countPostingProhibitedControls(@Param("budget") Budget budget);
    
    // Bulk operations
    @Modifying
    @Query("UPDATE BudgetControl bc SET bc.budgetCheckEnabled = :enabled, bc.lastUpdatedBy = :updatedBy, bc.lastUpdated = :now WHERE bc.id IN :controlIds")
    int updateBudgetCheckForControls(@Param("enabled") boolean enabled, @Param("updatedBy") String updatedBy, @Param("now") LocalDateTime now, @Param("controlIds") List<Long> controlIds);
    
    @Modifying
    @Query("UPDATE BudgetControl bc SET bc.budgetStopEnabled = :enabled, bc.lastUpdatedBy = :updatedBy, bc.lastUpdated = :now WHERE bc.id IN :controlIds")
    int updateBudgetStopForControls(@Param("enabled") boolean enabled, @Param("updatedBy") String updatedBy, @Param("now") LocalDateTime now, @Param("controlIds") List<Long> controlIds);
    
    @Modifying
    @Query("UPDATE BudgetControl bc SET bc.postingProhibited = :prohibited, bc.lastUpdatedBy = :updatedBy, bc.lastUpdated = :now WHERE bc.id IN :controlIds")
    int updatePostingProhibitedForControls(@Param("prohibited") boolean prohibited, @Param("updatedBy") String updatedBy, @Param("now") LocalDateTime now, @Param("controlIds") List<Long> controlIds);
    
    // Delete expired controls
    @Modifying
    @Query("DELETE FROM BudgetControl bc WHERE bc.controlStatus = 'EXPIRED' AND bc.expiryDate < :cutoffDate")
    int deleteExpiredControls(@Param("cutoffDate") LocalDateTime cutoffDate);
}

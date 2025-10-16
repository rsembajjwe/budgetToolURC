package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.RequisitionData;
import com.methaltech.application.data.entity.bgtool.RequisitionData.RequisitionStatus;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RequisitionDataRepository extends JpaRepository<RequisitionData, Long> {

    // Find by requisition number
    Optional<RequisitionData> findByRequisitionNumber(String requisitionNumber);

    // Find by budget
    /*    @Query("SELECT rd FROM RequisitionData rd LEFT JOIN FETCH rd.items WHERE rd.budget = :budget ORDER BY rd.createdDate DESC")
    List<RequisitionData> findByBudgetOrderByCreatedDateDesc(@Param("budget") Budget budget);*/
    @Query("""
    SELECT rd 
    FROM RequisitionData rd 
    LEFT JOIN FETCH rd.items 
    LEFT JOIN FETCH rd.coa 
    LEFT JOIN FETCH rd.createdBy
    LEFT JOIN FETCH rd.approvedBy             
    WHERE rd.budget = :budget 
    ORDER BY rd.createdDate DESC
""")
    List<RequisitionData> findByBudgetOrderByCreatedDateDesc(@Param("budget") Budget budget);

    @Query("""
    SELECT rd 
    FROM RequisitionData rd 
    LEFT JOIN FETCH rd.items 
    LEFT JOIN FETCH rd.coa 
    LEFT JOIN FETCH rd.createdBy
    LEFT JOIN FETCH rd.approvedBy       
    WHERE rd.budget = :budget 
      AND rd.status = :status
    ORDER BY rd.createdDate DESC
""")
    List<RequisitionData> findByBudgetAndStatusOrderByCreatedDateDesc(
            @Param("budget") Budget budget,
            @Param("status") RequisitionData.RequisitionStatus status);

    @Query("""
    SELECT r 
    FROM RequisitionData r
    LEFT JOIN FETCH r.items 
    LEFT JOIN FETCH r.coa 
    LEFT JOIN FETCH r.createdBy
    LEFT JOIN FETCH r.approvedBy             
    WHERE r.budget = :budget
      AND r.status IN :statuses
      AND r.deptSection IN :sections
    ORDER BY r.createdDate DESC
""")
    List<RequisitionData> findByBudgetStatusesAndDeptSectionsOrderByCreatedDateDesc(
            @Param("budget") Budget budget,
            @Param("statuses") Collection<RequisitionData.RequisitionStatus> statuses,
            @Param("sections") Collection<UrcDeptSectionAnlDimbgt> sections
    );

    @Query("""
    SELECT r 
    FROM RequisitionData r
    LEFT JOIN FETCH r.items 
    LEFT JOIN FETCH r.coa 
    LEFT JOIN FETCH r.createdBy
    LEFT JOIN FETCH r.approvedBy             
    WHERE r.budget = :budget
      AND r.status = :status
      AND r.deptSection = :section
    ORDER BY r.createdDate DESC
""")
    List<RequisitionData> findByBudgetStatuseAndDeptSectionOrderByCreatedDateDesc(
            @Param("budget") Budget budget,
            @Param("status") RequisitionData.RequisitionStatus statuses,
            @Param("section") UrcDeptSectionAnlDimbgt sections
    );

// 1️⃣ Find by Budget and DeptSection
    @Query("""
    SELECT rd 
    FROM RequisitionData rd 
    LEFT JOIN FETCH rd.items 
    LEFT JOIN FETCH rd.coa
    LEFT JOIN FETCH rd.createdBy
    LEFT JOIN FETCH rd.approvedBy              
    WHERE rd.budget = :budget 
      AND rd.deptSection = :deptSection 
    ORDER BY rd.createdDate DESC
""")
    List<RequisitionData> findByBudgetAndDeptSectionOrderByCreatedDateDesc(
            @Param("budget") Budget budget,
            @Param("deptSection") UrcDeptSectionAnlDimbgt deptSection
    );

// 2️⃣ Find by Budget and a set of DeptSections
    @Query("SELECT rd FROM RequisitionData rd LEFT JOIN FETCH rd.items LEFT JOIN FETCH rd.coa LEFT JOIN FETCH rd.createdBy LEFT JOIN FETCH rd.approvedBy "
            + "WHERE rd.budget = :budget AND rd.deptSection IN :deptSections "
            + "ORDER BY rd.createdDate DESC")
    List<RequisitionData> findByBudgetAndDeptSectionInOrderByCreatedDateDesc(
            @Param("budget") Budget budget,
            @Param("deptSections") Set<UrcDeptSectionAnlDimbgt> deptSections
    );

    // Find by activity
@Query("""
    SELECT rd FROM RequisitionData rd
    LEFT JOIN FETCH rd.items
    LEFT JOIN FETCH rd.coa
    LEFT JOIN FETCH rd.createdBy
    LEFT JOIN FETCH rd.approvedBy
    WHERE rd.activity = :activity
    ORDER BY rd.createdDate DESC
""")
List<RequisitionData> findByActivityOrderByCreatedDateDesc(@Param("activity") Urc_Activities activity);


    // Find by account code
    //List<RequisitionData> findByAccountCodeOrderByCreatedDateDesc(String accountCode);
    // Find by creator
    //List<RequisitionData> findByCreatedByOrderByCreatedDateDesc(String createdBy);
    // Find by status
    List<RequisitionData> findByStatusOrderByCreatedDateDesc(RequisitionData.RequisitionStatus status);

    // Find by requisition type
    List<RequisitionData> findByRequisitionTypeOrderByCreatedDateDesc(RequisitionData.RequisitionType requisitionType);

    // Find by budget and activity
    @Query("""
    SELECT rd FROM RequisitionData rd
    LEFT JOIN FETCH rd.items
    LEFT JOIN FETCH rd.coa
    LEFT JOIN FETCH rd.createdBy
    LEFT JOIN FETCH rd.approvedBy
    WHERE rd.activity = :activity
""")
    List<RequisitionData> findByActivity(@Param("activity") Urc_Activities activity);

    // Find by budget and status
    // List<RequisitionData> findByBudgetAndStatusOrderByCreatedDateDesc(Budget budget, RequisitionData.RequisitionStatus status);
    // Find recent requisitions
    @Query("SELECT rd FROM RequisitionData rd WHERE rd.createdDate >= :since ORDER BY rd.createdDate DESC")
    List<RequisitionData> findRecentRequisitions(@Param("since") LocalDateTime since);

    // Search requisitions
    @Query("""
  SELECT rd 
  FROM RequisitionData rd 
  WHERE LOWER(rd.requisitionNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
     OR LOWER(rd.purpose) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
  ORDER BY rd.createdDate DESC
""")
    Page<RequisitionData> searchRequisitions(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Count by status
    Long countByStatus(RequisitionData.RequisitionStatus status);

    // Count by budget
    Long countByBudget(Budget budget);

    // Count by activity
    Long countByActivity(Urc_Activities activity);

    // Count requisitions by Budget and Procurement Method
    Long countByBudgetAndProcMethods(Budget budget, RequisitionData.ProcMethods procMethods);

    Long countByBudgetAndDeptSectionAndProcMethods(
            Budget budget,
            UrcDeptSectionAnlDimbgt deptSection,
            RequisitionData.ProcMethods procMethods
    );

    Long countByBudgetAndDeptSection(
            Budget budget,
            UrcDeptSectionAnlDimbgt deptSection
    );

    Long countByBudgetAndStatusAndDeptSection(Budget budget,
            RequisitionStatus status,
            UrcDeptSectionAnlDimbgt deptSection);

    Long countByBudgetAndStatus(Budget budget,
            RequisitionStatus status);

    // Sum amounts by status
    @Query("SELECT SUM(rd.totalAmount) FROM RequisitionData rd WHERE rd.status = :status")
    Double sumAmountByStatus(@Param("status") RequisitionData.RequisitionStatus status);

    // Sum amounts by budget
    @Query("SELECT SUM(rd.totalAmount) FROM RequisitionData rd WHERE rd.budget = :budget")
    Double sumAmountByBudget(@Param("budget") Budget budget);

    // Sum amounts by activity
    @Query("SELECT SUM(rd.totalAmount) FROM RequisitionData rd WHERE rd.activity = :activity")
    Double sumAmountByActivity(@Param("activity") Urc_Activities activity);

    // Find by date range
    @Query("SELECT rd FROM RequisitionData rd WHERE rd.createdDate BETWEEN :startDate AND :endDate ORDER BY rd.createdDate DESC")
    List<RequisitionData> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Find by amount range
    @Query("SELECT rd FROM RequisitionData rd WHERE rd.totalAmount BETWEEN :minAmount AND :maxAmount ORDER BY rd.totalAmount DESC")
    List<RequisitionData> findByAmountRange(@Param("minAmount") Double minAmount, @Param("maxAmount") Double maxAmount);

    // Count requisitions by type
    @Query("SELECT rd.requisitionType, COUNT(rd) FROM RequisitionData rd GROUP BY rd.requisitionType")
    List<Object[]> countRequisitionsByType();

    // Count requisitions by month
    @Query("SELECT YEAR(rd.createdDate), MONTH(rd.createdDate), COUNT(rd) FROM RequisitionData rd GROUP BY YEAR(rd.createdDate), MONTH(rd.createdDate) ORDER BY YEAR(rd.createdDate) DESC, MONTH(rd.createdDate) DESC")
    List<Object[]> countRequisitionsByMonth();

    // Find pending approvals
    @Query("SELECT rd FROM RequisitionData rd WHERE rd.status = 'SUBMITTED' ORDER BY rd.createdDate ASC")
    List<RequisitionData> findPendingApprovals();

    // Find over budget requisitions
    @Query("SELECT rd FROM RequisitionData rd WHERE rd.totalAmount > rd.availableBalanceByActivity ORDER BY rd.createdDate DESC")
    List<RequisitionData> findOverBudgetRequisitions();

    List<RequisitionData> findByBudget(Budget budget);

    @Query("SELECT r FROM RequisitionData r "
            + "WHERE r.requisitionNumber LIKE %:startYear% "
            + "AND r.requisitionNumber LIKE %:endYear% "
            + "ORDER BY r.id DESC")
    List<RequisitionData> findLastRequisitionForFiscal(
            @Param("startYear") String startYear,
            @Param("endYear") String endYear,
            Pageable pageable
    );

    // Sum by Budget
    @Query("SELECT COALESCE(SUM(r.requestedAmount), 0) "
            + "FROM RequisitionData r "
            + "WHERE r.budget = :budget")
    BigDecimal sumRequestedAmountByBudget(@Param("budget") Budget budget);

    // Sum by DeptSection
    @Query("SELECT COALESCE(SUM(r.requestedAmount), 0) "
            + "FROM RequisitionData r "
            + "WHERE r.deptSection = :deptSection")
    BigDecimal sumRequestedAmountByDeptSection(@Param("deptSection") UrcDeptSectionAnlDimbgt deptSection);

    @Query("SELECT COALESCE(SUM(r.requestedAmount), 0) "
            + "FROM RequisitionData r "
            + "WHERE r.budget = :budget "
            + "AND r.deptSection = :deptSection "
            + "AND r.status = :status")
    BigDecimal sumRequestedAmountByBudgetDeptSectionAndStatus(@Param("budget") Budget budget,
            @Param("deptSection") UrcDeptSectionAnlDimbgt deptSection,
            @Param("status") RequisitionStatus status);

    @Query("SELECT COALESCE(SUM(r.requestedAmount), 0) "
            + "FROM RequisitionData r "
            + "WHERE r.budget = :budget "
            + "AND r.coa.code = :coaCode "
            + "AND r.deptSection = :deptSection "
            + "AND r.activity.id = :activityId AND r.status = :status AND r.id <> :excludeId")
    BigDecimal sumRequestedAmountByBudgetCOADeptSectionAndActivity(@Param("budget") Budget budget, @Param("coaCode") String coaCode,
            @Param("deptSection") UrcDeptSectionAnlDimbgt deptSection,
            @Param("activityId") Long activityId, @Param("status") RequisitionStatus status, @Param("excludeId") Long excludeId);

    @Query("SELECT COALESCE(SUM(r.requestedAmount), 0) "
            + "FROM RequisitionData r "
            + "WHERE r.budget = :budget "
            + "AND r.coa.code = :coaCode "
            + "AND r.deptSection = :deptSection  AND r.status = :status  AND r.id <> :excludeId")
    BigDecimal sumRequestedAmountByBudgetCOADeptSection(@Param("budget") Budget budget, @Param("coaCode") String coaCode,
            @Param("deptSection") UrcDeptSectionAnlDimbgt deptSection, @Param("status") RequisitionStatus status, @Param("excludeId") Long excludeId);

    // Sum by Budget + Status
    @Query("SELECT COALESCE(SUM(r.requestedAmount), 0) "
            + "FROM RequisitionData r "
            + "WHERE r.budget = :budget AND r.status = :status")
    BigDecimal sumRequestedAmountByBudgetAndStatus(@Param("budget") Budget budget,
            @Param("status") RequisitionStatus status);

    @Query("SELECT COALESCE(SUM(r.requestedAmount), 0) "
            + "FROM RequisitionData r "
            + "WHERE r.coa.code = :coaCode AND r.activity.id = :activityId")
    Double getRequestedSumByActivityAndCoa(@Param("coaCode") String coaCode,
            @Param("activityId") Long activityId);

    @Query("SELECT COALESCE(SUM(r.requestedAmount), 0) "
            + "FROM RequisitionData r "
            + "WHERE r.coa.code = :coaCode "
            + "AND r.activity.id = :activityId "
            + "AND r.status <> :excludedStatus")
    Double getRequestedSumByActivityAndCoa(@Param("coaCode") String coaCode,
            @Param("activityId") Long activityId,
            @Param("excludedStatus") RequisitionData.RequisitionStatus excludedStatus);

    @Query("SELECT COALESCE(SUM(r.requestedAmount), 0) "
            + "FROM RequisitionData r "
            + "WHERE r.activity.id = :activityId "
            + "AND r.status <> :status")
    Double findTotalRequestedAmountByActivityExcludingStatus(
            @Param("activityId") Long activityId,
            @Param("status") RequisitionData.RequisitionStatus status
    );

    @Query("SELECT r FROM RequisitionData r LEFT JOIN FETCH r.items LEFT JOIN FETCH r.coa LEFT JOIN FETCH r.createdBy WHERE r.id = :id")
    Optional<RequisitionData> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT r FROM RequisitionData r "
            + "LEFT JOIN FETCH r.items "
            + "LEFT JOIN FETCH r.coa "
            + "LEFT JOIN FETCH r.createdBy "
            + "WHERE r.requisitionNumber = :requisitionNumber")
    Optional<RequisitionData> findWithItemsAndCoaAndUserByRequisitionNumber(String requisitionNumber);
}

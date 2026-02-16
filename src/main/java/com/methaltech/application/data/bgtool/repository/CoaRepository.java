package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.Classification2;
import com.methaltech.application.data.Classification3;
import com.methaltech.application.data.Display;
import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Coalevel11;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.Section;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoaRepository extends JpaRepository<COA, Long> {

    List<COA> findByBudgetAndCoalevel1(Budget budget, Coalevel1 coalevel1);

    List<COA> findByBudgetAndCoalevel11(Budget budget, Coalevel11 coalevel11);

    List<COA> findByBudgetAndCoalevel1AndNameContainingIgnoreCaseOrCodeContainingIgnoreCase(Budget budget, Coalevel1 coalevel1, String name, String code);

    //@Query("SELECT c FROM COA c WHERE c.budget = :budget AND c.coalevel1 = :coalevel1 AND (SUBSTRING(c.name, 3) LIKE %:substring% OR SUBSTRING(c.code, 3) LIKE %:substring%)")
    @Query("SELECT c FROM COA c WHERE c.budget = :budget AND c.coalevel1 = :coalevel1 AND (SUBSTRING(c.name, 1, LEN(c.name)) LIKE '%' + :substring + '%' OR SUBSTRING(c.code, 1, LEN(c.code)) LIKE '%' + :substring + '%')")
    List<COA> findByBudgetAndCoalevel1AndSubstring(@Param("budget") Budget budget, @Param("coalevel1") Coalevel1 coalevel1, @Param("substring") String substring);

    //boolean existsByCode(String code);
    boolean existsByCodeAndBudget(String code, Budget budget);

    boolean existsByBudget(Budget budget);

    boolean existsBy();

    List<COA> findByBudget(Budget oldbudget);
    // List<COA> findByCode(String code);

    List<COA> findByBudgetIdAndDisplayOrderByCodeAsc(Long budgetId, Display display);

    //COA findByCodeAndBudget(String code, Budget budget);
    @Query("SELECT c FROM COA c LEFT JOIN FETCH c.dsections WHERE c.code = :code AND c.stateOpen = true AND c.budget = :budget")
    COA findByCodeAndBudgetWithDSections(@Param("code") String code, @Param("budget") Budget budget);

    @Query("SELECT c FROM COA c JOIN c.dsections ds WHERE c.budget = :budget AND c.stateOpen = true")
    List<COA> findByBudgetAndDSections(@Param("budget") Budget budget);

    @Query("SELECT c FROM COA c JOIN c.deptsection dept WHERE dept IN :deptsection AND c.code LIKE :codePrefix% AND c.budget = :budget AND c.stateOpen = true")
    List<COA> findByDeptsectionAndCodePrefix(@Param("deptsection") Set<UrcDeptSectionAnlDimbgt> deptsection, @Param("codePrefix") String codePrefix, @Param("budget") Budget budget);

    @Query("SELECT c FROM COA c JOIN c.deptsection dept WHERE dept IN :deptsection AND c.code LIKE :codePrefix% AND ((LOWER(c.code) LIKE LOWER(CONCAT('%', :category, '%')) OR LOWER(c.name) LIKE LOWER(CONCAT('%', :category, '%'))) OR (c.name IS NULL AND :category IS NULL)) AND c.budget = :budget AND c.stateOpen = true")
    List<COA> findByDeptsectionAndCodePrefixSearch(@Param("deptsection") Set<UrcDeptSectionAnlDimbgt> deptsection, @Param("codePrefix") String codePrefix, @Param("category") String category, @Param("budget") Budget budget);

    @Query("SELECT c FROM COA c "
            + "JOIN c.deptsection dept "
            + "WHERE dept IN :deptsection "
            + "AND c.coalevel1 = :coalevel1 "
            + "AND c.budget = :budget "
            + "AND c.stateOpen = true "
            + "AND (:searchTerm is null OR "
            + "     LOWER(c.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "     LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<COA> findByDeptsectionAndCoalevel1AndBudgetAndSearchTerm(
            @Param("deptsection") Set<UrcDeptSectionAnlDimbgt> deptsection,
            @Param("coalevel1") Coalevel1 coalevel1,
            @Param("budget") Budget budget,
            @Param("searchTerm") String searchTerm);

    @Query("SELECT c FROM COA c "
            + "JOIN c.dsections ds "
            + "JOIN c.coalevel12 cl12 "
            + "WHERE ds IN :sections "
            + "AND c.code LIKE :category% "
            + "AND (c.code LIKE CONCAT('%', :codePrefix, '%') OR c.name LIKE CONCAT('%', :codePrefix, '%')) "
            + "AND cl12.name = 'GENERAL' "
            + "AND c.budget = :budget AND c.stateOpen = true")
    List<COA> findBySectionsAndCodePrefixSearch2(
            @Param("sections") Set<Section> sections,
            @Param("codePrefix") String codePrefix,
            @Param("category") String category,
            @Param("budget") Budget budget
    );

    @Query("SELECT c FROM COA c "
            + "JOIN c.deptsection ds "
            + "WHERE ds = :deptSection "
            + "AND c.code LIKE :codeFilter% "
            + "AND c.budget = :budget "
            + "AND c.stateOpen = true")
    List<COA> findByDeptSectionAndCodeStartingWithAndStateOpen(
            @Param("deptSection") UrcDeptSectionAnlDimbgt deptSection,
            @Param("codeFilter") String codeFilter,
            @Param("budget") Budget budget
    );

    @Query("SELECT c FROM COA c "
            + "JOIN c.deptsection ds "
            + "WHERE ds = :deptSection "
            + "AND (c.code LIKE '2%' OR c.code LIKE '3%') "
            + "AND c.budget = :budget "
            + "AND c.stateOpen = true")
    List<COA> findByDeptSectionAndCodeStartingWith2Or3AndStateOpen(
            @Param("deptSection") UrcDeptSectionAnlDimbgt deptSection,
            @Param("budget") Budget budget
    );

    List<COA> findByBudgetAndDisplay(Budget budget, Display display);

    @Query("SELECT c FROM COA c WHERE c.code = :code AND c.budget = :budget AND c.stateOpen = true")
    COA findByCodeAndBudget(@Param("code") String code, @Param("budget") Budget budget);

    @Query("SELECT c FROM COA c WHERE c.code = :code AND c.budget = :budget AND c.stateOpen = true")
    List<COA> findByCodeAndBudget2(@Param("code") String code, @Param("budget") Budget budget);

    List<COA> findByBudgetAndDisplayAndStateOpen(Budget budget, Display display, boolean stateOpen);

    // Custom method to find COA by Budget and List of ProcClass
    List<COA> findByBudgetAndProcclassIn(Budget budget, List<ProcClass> procclasses);

    List<COA> findByBudgetAndProcclass(Budget budget, ProcClass procclasses);

    List<COA> findByBudgetAndCodeStartingWith(Budget budget, String codePrefix);

    // ===================================================================
    // BASIC FINDER METHODS
    // ===================================================================
    // Find by code
    Optional<COA> findByCode(String code);

    // Find by name
    List<COA> findByNameContainingIgnoreCaseOrderByCodeAsc(String name);

    // Find by code containing
    List<COA> findByCodeContainingIgnoreCaseOrderByCodeAsc(String code);

    // ===================================================================
    // BUDGET-RELATED QUERIES
    // ===================================================================
    // Find by budget
    List<COA> findByBudgetOrderByCodeAsc(Budget budget);

    // Find by budget and active state
    List<COA> findByBudgetAndStateOpenTrueOrderByCodeAsc(Budget budget);

    // Find by budget and inactive state
    List<COA> findByBudgetAndStateOpenFalseOrderByCodeAsc(Budget budget);

    // ===================================================================
    // ORGANISATION-RELATED QUERIES
    // ===================================================================
    // Find by organisation
    List<COA> findByOrganisationOrderByCodeAsc(Organisation organisation);

    // Find unassigned COA by budget
    List<COA> findByBudgetAndOrganisationIsNullOrderByCodeAsc(Budget budget);

    // Find assigned COA by budget
    List<COA> findByBudgetAndOrganisationIsNotNullOrderByCodeAsc(Budget budget);

    // Find by budget and organisation
    @Query("SELECT c FROM COA c WHERE c.budget = :budget AND c.organisation = :organisation ORDER BY c.code ASC")
    List<COA> findByBudgetAndOrganisation(@Param("budget") Budget budget, @Param("organisation") Organisation organisation);

    // ===================================================================
    // STATE-RELATED QUERIES
    // ===================================================================
    // Find by active state
    List<COA> findByStateOpenTrueOrderByCodeAsc();

    // Find by inactive state
    List<COA> findByStateOpenFalseOrderByCodeAsc();

    // ===================================================================
    // HIERARCHY-RELATED QUERIES
    // ===================================================================
    // Find by coalevel1
    List<COA> findByCoalevel1_IdOrderByCodeAsc(Long coalevel1Id);

    // Find by coalevel11
    List<COA> findByCoalevel11_IdOrderByCodeAsc(Long coalevel11Id);

    // Find by coalevel12
    List<COA> findByCoalevel12_IdOrderByCodeAsc(Long coalevel12Id);

    // Find by coalevel13
    List<COA> findByCoalevel13_IdOrderByCodeAsc(Long coalevel13Id);

    // Find by hierarchy levels
    @Query("SELECT c FROM COA c WHERE "
            + "(:coalevel1Id IS NULL OR c.coalevel1.id = :coalevel1Id) AND "
            + "(:coalevel11Id IS NULL OR c.coalevel11.id = :coalevel11Id) AND "
            + "(:coalevel12Id IS NULL OR c.coalevel12.id = :coalevel12Id) AND "
            + "(:coalevel13Id IS NULL OR c.coalevel13.id = :coalevel13Id) "
            + "ORDER BY c.code ASC")
    List<COA> findByHierarchy(@Param("coalevel1Id") Long coalevel1Id,
            @Param("coalevel11Id") Long coalevel11Id,
            @Param("coalevel12Id") Long coalevel12Id,
            @Param("coalevel13Id") Long coalevel13Id);

    // ===================================================================
    // SEARCH QUERIES
    // ===================================================================
    // Search COA by multiple criteria
    @Query("SELECT c FROM COA c WHERE "
            + "LOWER(c.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
            + "ORDER BY c.code ASC")
    Page<COA> searchCOA(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Search COA by budget
    @Query("SELECT c FROM COA c WHERE c.budget = :budget AND ("
            + "LOWER(c.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) "
            + "ORDER BY c.code ASC")
    Page<COA> searchCOAByBudget(@Param("budget") Budget budget, @Param("searchTerm") String searchTerm, Pageable pageable);

    // Search COA by organisation
    @Query("SELECT c FROM COA c WHERE c.organisation = :organisation AND ("
            + "LOWER(c.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) "
            + "ORDER BY c.code ASC")
    Page<COA> searchCOAByOrganisation(@Param("organisation") Organisation organisation, @Param("searchTerm") String searchTerm, Pageable pageable);

    // Advanced search with multiple criteria
    @Query("SELECT c FROM COA c WHERE "
            + "(:budget IS NULL OR c.budget = :budget) AND "
            + "(:organisation IS NULL OR c.organisation = :organisation) AND "
            + "(:stateOpen IS NULL OR c.stateOpen = :stateOpen) AND "
            + "(:searchTerm IS NULL OR "
            + "LOWER(c.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) "
            + "ORDER BY c.code ASC")
    List<COA> findByCriteria(@Param("budget") Budget budget,
            @Param("organisation") Organisation organisation,
            @Param("stateOpen") Boolean stateOpen,
            @Param("searchTerm") String searchTerm);

    // ===================================================================
    // COUNT QUERIES
    // ===================================================================
    // Count by state
    Long countByStateOpenTrue();

    Long countByStateOpenFalse();

    // Count by organisation assignment
    Long countByOrganisationIsNotNull();

    Long countByOrganisationIsNull();

    // Count by budget
    Long countByBudget(Budget budget);

    // Count by budget and state
    Long countByBudgetAndStateOpenTrue(Budget budget);

    Long countByBudgetAndStateOpenFalse(Budget budget);

    // Count by budget and organisation assignment
    Long countByBudgetAndOrganisationIsNotNull(Budget budget);

    Long countByBudgetAndOrganisationIsNull(Budget budget);

    // ===================================================================
    // STATISTICAL QUERIES
    // ===================================================================
    // Count COA by organisation
    @Query("SELECT o.name, COUNT(c) FROM COA c JOIN c.organisation o GROUP BY o.id, o.name ORDER BY COUNT(c) DESC")
    List<Object[]> countCOAByOrganisation();

    // Count COA by coalevel1
    @Query("SELECT cl1.name, COUNT(c) FROM COA c JOIN c.coalevel1 cl1 GROUP BY cl1.id, cl1.name ORDER BY COUNT(c) DESC")
    List<Object[]> countCOAByCoalevel1();

    // Count COA by coalevel11
    @Query("SELECT cl11.name, COUNT(c) FROM COA c JOIN c.coalevel11 cl11 GROUP BY cl11.id, cl11.name ORDER BY COUNT(c) DESC")
    List<Object[]> countCOAByCoalevel11();

    // Count COA by coalevel12
    @Query("SELECT cl12.name, COUNT(c) FROM COA c JOIN c.coalevel12 cl12 GROUP BY cl12.id, cl12.name ORDER BY COUNT(c) DESC")
    List<Object[]> countCOAByCoalevel12();

    // Count COA by coalevel13
    @Query("SELECT cl13.name, COUNT(c) FROM COA c JOIN c.coalevel13 cl13 GROUP BY cl13.id, cl13.name ORDER BY COUNT(c) DESC")
    List<Object[]> countCOAByCoalevel13();

    // Count COA by budget and organisation
    @Query("SELECT o.name, COUNT(c) FROM COA c JOIN c.organisation o WHERE c.budget = :budget GROUP BY o.id, o.name ORDER BY COUNT(c) DESC")
    List<Object[]> countCOAByOrganisationAndBudget(@Param("budget") Budget budget);

    // ===================================================================
    // ASSIGNMENT MANAGEMENT QUERIES
    // ===================================================================
    // Find COA available for assignment (active and unassigned)
    @Query("SELECT c FROM COA c WHERE c.budget = :budget AND c.stateOpen = true AND c.organisation IS NULL ORDER BY c.code ASC")
    List<COA> findAvailableForAssignment(@Param("budget") Budget budget);

    // Find COA by organisation with eager loading
    @Query("SELECT c FROM COA c LEFT JOIN FETCH c.organisation WHERE c.organisation.id = :organisationId ORDER BY c.code ASC")
    List<COA> findByOrganisationIdWithOrganisation(@Param("organisationId") Long organisationId);

    // Find COA by budget with organisation info
    @Query("SELECT c FROM COA c LEFT JOIN FETCH c.organisation WHERE c.budget = :budget ORDER BY c.code ASC")
    List<COA> findByBudgetWithOrganisation(@Param("budget") Budget budget);

    // ===================================================================
    // BULK OPERATIONS
    // ===================================================================
    // Update organisation for multiple COA
    @Query("UPDATE COA c SET c.organisation = :organisation WHERE c.id IN :coaIds")
    int updateOrganisationForCOAs(@Param("organisation") Organisation organisation, @Param("coaIds") List<Long> coaIds);

    // Remove organisation assignment for multiple COA
    @Query("UPDATE COA c SET c.organisation = NULL WHERE c.id IN :coaIds")
    int removeOrganisationForCOAs(@Param("coaIds") List<Long> coaIds);

    // Update state for multiple COA
    @Query("UPDATE COA c SET c.stateOpen = :stateOpen WHERE c.id IN :coaIds")
    int updateStateForCOAs(@Param("stateOpen") boolean stateOpen, @Param("coaIds") List<Long> coaIds);

    // ===================================================================
    // VALIDATION QUERIES
    // ===================================================================
    // Check if code exists
    boolean existsByCode(String code);

    // Check if code exists for different COA
    @Query("SELECT COUNT(c) > 0 FROM COA c WHERE c.code = :code AND c.id != :id")
    boolean existsByCodeAndIdNot(@Param("code") String code, @Param("id") Long id);

    // Find duplicate codes
    @Query("SELECT c.code, COUNT(c) FROM COA c GROUP BY c.code HAVING COUNT(c) > 1")
    List<Object[]> findDuplicateCodes();

    // ===================================================================
    // REPORTING QUERIES
    // ===================================================================
    // COA utilization report
    @Query("SELECT "
            + "c.budget.financialYear as budgetYear, "
            + "COUNT(c) as totalCOA, "
            + "SUM(CASE WHEN c.organisation IS NOT NULL THEN 1 ELSE 0 END) as assignedCOA, "
            + "SUM(CASE WHEN c.stateOpen = true THEN 1 ELSE 0 END) as activeCOA "
            + "FROM COA c GROUP BY c.budget.id, c.budget.financialYear ORDER BY c.budget.financialYear DESC")
    List<Object[]> getCOAUtilizationReport();

    // Organisation assignment summary
    @Query("SELECT "
            + "o.name as organisationName, "
            + "o.code as organisationCode, "
            + "COUNT(c) as coaCount, "
            + "SUM(CASE WHEN c.stateOpen = true THEN 1 ELSE 0 END) as activeCOACount "
            + "FROM COA c JOIN c.organisation o "
            + "WHERE c.budget = :budget "
            + "GROUP BY o.id, o.name, o.code "
            + "ORDER BY COUNT(c) DESC")
    List<Object[]> getOrganisationAssignmentSummary(@Param("budget") Budget budget);

    // Hierarchy distribution report
    @Query("SELECT "
            + "cl1.name as level1Name, "
            + "cl11.name as level11Name, "
            + "COUNT(c) as coaCount "
            + "FROM COA c "
            + "LEFT JOIN c.coalevel1 cl1 "
            + "LEFT JOIN c.coalevel11 cl11 "
            + "WHERE c.budget = :budget "
            + "GROUP BY cl1.id, cl1.name, cl11.id, cl11.name "
            + "ORDER BY COUNT(c) DESC")
    List<Object[]> getHierarchyDistributionReport(@Param("budget") Budget budget);

    // ===================================================================
    // DASHBOARD STATISTICS
    // ===================================================================
    // Get COA statistics for dashboard
    @Query("SELECT "
            + "COUNT(c) as totalCount, "
            + "SUM(CASE WHEN c.stateOpen = true THEN 1 ELSE 0 END) as activeCount, "
            + "SUM(CASE WHEN c.organisation IS NOT NULL THEN 1 ELSE 0 END) as assignedCount, "
            + "SUM(CASE WHEN c.organisation IS NULL THEN 1 ELSE 0 END) as unassignedCount "
            + "FROM COA c WHERE c.budget = :budget")
    Object[] getCOAStatistics(@Param("budget") Budget budget);

    // Get organisation assignment statistics
    @Query("SELECT "
            + "COUNT(DISTINCT o) as organisationsWithCOA, "
            + "COUNT(c) as totalAssignments, "
            + "AVG(orgCounts.coaCount) as avgCOAPerOrg "
            + "FROM COA c "
            + "JOIN c.organisation o, "
            + "(SELECT org.id as orgId, COUNT(coa) as coaCount "
            + " FROM COA coa JOIN coa.organisation org "
            + " WHERE coa.budget = :budget "
            + " GROUP BY org.id) orgCounts "
            + "WHERE c.budget = :budget AND o.id = orgCounts.orgId")
    Object[] getOrganisationAssignmentStatistics(@Param("budget") Budget budget);

    // ===================================================================
    // COMPLEX FILTERING QUERIES
    // ===================================================================
    // Find COA by multiple criteria with pagination
    @Query("SELECT c FROM COA c WHERE "
            + "(:budget IS NULL OR c.budget = :budget) AND "
            + "(:organisation IS NULL OR c.organisation = :organisation) AND "
            + "(:stateOpen IS NULL OR c.stateOpen = :stateOpen) AND "
            + "(:coalevel1Id IS NULL OR c.coalevel1.id = :coalevel1Id) AND "
            + "(:coalevel11Id IS NULL OR c.coalevel11.id = :coalevel11Id) AND "
            + "(:searchTerm IS NULL OR "
            + "LOWER(c.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) "
            + "ORDER BY c.code ASC")
    Page<COA> findByMultipleCriteria(@Param("budget") Budget budget,
            @Param("organisation") Organisation organisation,
            @Param("stateOpen") Boolean stateOpen,
            @Param("coalevel1Id") Long coalevel1Id,
            @Param("coalevel11Id") Long coalevel11Id,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    // Find COA for assignment with filters
    @Query("SELECT c FROM COA c WHERE "
            + "c.budget = :budget AND "
            + "c.stateOpen = true AND "
            + "c.organisation IS NULL AND "
            + "(:coalevel1Id IS NULL OR c.coalevel1.id = :coalevel1Id) AND "
            + "(:searchTerm IS NULL OR "
            + "LOWER(c.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) "
            + "ORDER BY c.code ASC")
    List<COA> findAvailableForAssignmentWithFilters(@Param("budget") Budget budget,
            @Param("coalevel1Id") Long coalevel1Id,
            @Param("searchTerm") String searchTerm);

    // ===================================================================
    // MAINTENANCE QUERIES
    // ===================================================================
    // Find orphaned COA (no budget)
    @Query("SELECT c FROM COA c WHERE c.budget IS NULL ORDER BY c.code ASC")
    List<COA> findOrphanedCOA();

    // Find COA with missing hierarchy
    @Query("SELECT c FROM COA c WHERE c.coalevel1 IS NULL ORDER BY c.code ASC")
    List<COA> findCOAWithoutLevel1();

    // Find inactive COA with assignments
    @Query("SELECT c FROM COA c WHERE c.stateOpen = false AND c.organisation IS NOT NULL ORDER BY c.code ASC")
    List<COA> findInactiveCOAWithAssignments();

    // ===================================================================
    // AUDIT QUERIES
    // ===================================================================
    // Find recently modified COA (if you have audit fields)
    @Query("SELECT c FROM COA c WHERE c.budget = :budget ORDER BY c.id DESC")
    List<COA> findRecentlyModified(@Param("budget") Budget budget, Pageable pageable);

    // Find COA by creation pattern
    @Query("SELECT c FROM COA c WHERE c.code LIKE :pattern ORDER BY c.code ASC")
    List<COA> findByCodePattern(@Param("pattern") String pattern);

    // ===================================================================
    // PERFORMANCE QUERIES
    // ===================================================================
    // Find COA with minimal data for dropdowns
    @Query("SELECT NEW com.methaltech.application.data.entity.bgtool.COA(c.id, c.code, c.name) FROM COA c WHERE c.budget = :budget AND c.stateOpen = true ORDER BY c.code ASC")
    List<COA> findMinimalCOAForDropdown(@Param("budget") Budget budget);

    // Find COA codes only
    @Query("SELECT c.code FROM COA c WHERE c.budget = :budget AND c.stateOpen = true ORDER BY c.code ASC")
    List<String> findCOACodesByBudget(@Param("budget") Budget budget);

    // Find COA names only
    @Query("SELECT c.name FROM COA c WHERE c.budget = :budget AND c.stateOpen = true ORDER BY c.name ASC")
    List<String> findCOANamesByBudget(@Param("budget") Budget budget);

    // ===================================================================
    // BATCH OPERATIONS
    // ===================================================================
    // Activate multiple COA
    @Query("UPDATE COA c SET c.stateOpen = true WHERE c.id IN :coaIds")
    int activateCOAs(@Param("coaIds") List<Long> coaIds);

    // Deactivate multiple COA
    @Query("UPDATE COA c SET c.stateOpen = false WHERE c.id IN :coaIds")
    int deactivateCOAs(@Param("coaIds") List<Long> coaIds);

    // Bulk assign to organisation
    @Query("UPDATE COA c SET c.organisation = :organisation WHERE c.id IN :coaIds AND c.stateOpen = true")
    int bulkAssignToOrganisation(@Param("organisation") Organisation organisation, @Param("coaIds") List<Long> coaIds);

    // Bulk unassign from organisation
    @Query("UPDATE COA c SET c.organisation = NULL WHERE c.organisation = :organisation")
    int bulkUnassignFromOrganisation(@Param("organisation") Organisation organisation);

    // ===================================================================
    // VALIDATION AND INTEGRITY QUERIES
    // ===================================================================
    // Check for circular references in hierarchy
    @Query("SELECT c FROM COA c WHERE "
            + "c.coalevel1.id = c.coalevel11.id OR "
            + "c.coalevel1.id = c.coalevel12.id OR "
            + "c.coalevel1.id = c.coalevel13.id OR "
            + "c.coalevel11.id = c.coalevel12.id OR "
            + "c.coalevel11.id = c.coalevel13.id OR "
            + "c.coalevel12.id = c.coalevel13.id")
    List<COA> findCOAWithCircularHierarchy();

    // Find COA with inconsistent state
    @Query("SELECT c FROM COA c WHERE c.stateOpen = false AND c.organisation IS NOT NULL")
    List<COA> findInconsistentCOAState();

    // ===================================================================
    // EXPORT QUERIES
    // ===================================================================
    // Export COA for budget
    @Query("SELECT c FROM COA c "
            + "LEFT JOIN FETCH c.organisation "
            + "LEFT JOIN FETCH c.coalevel1 "
            + "LEFT JOIN FETCH c.coalevel11 "
            + "LEFT JOIN FETCH c.coalevel12 "
            + "LEFT JOIN FETCH c.coalevel13 "
            + "WHERE c.budget = :budget "
            + "ORDER BY c.code ASC")
    List<COA> findForExport(@Param("budget") Budget budget);

    // Export assigned COA only
    @Query("SELECT c FROM COA c "
            + "LEFT JOIN FETCH c.organisation "
            + "WHERE c.budget = :budget AND c.organisation IS NOT NULL "
            + "ORDER BY c.organisation.name ASC, c.code ASC")
    List<COA> findAssignedForExport(@Param("budget") Budget budget);

    // Export unassigned COA only
    @Query("SELECT c FROM COA c "
            + "WHERE c.budget = :budget AND c.organisation IS NULL AND c.stateOpen = true "
            + "ORDER BY c.code ASC")
    List<COA> findUnassignedForExport(@Param("budget") Budget budget);

    List<COA> findByCodeIn(Collection<String> codes);

    List<COA> findByBudgetAndClass3(Budget budget, Classification3 class3);
    List<COA> findByBudgetAndClass2(Budget budget, Classification2 class2);

}

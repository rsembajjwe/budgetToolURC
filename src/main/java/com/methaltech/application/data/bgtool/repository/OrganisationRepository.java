package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Organisation;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

    /*    List<Organisation> findByBudget(Budget budget);
    
    Page<Organisation> findByBudget(Budget budget, Pageable pageable);*/
    @Query("SELECT DISTINCT o FROM Organisation o LEFT JOIN FETCH o.coaAccounts WHERE o.budget = :budget")
    List<Organisation> findByBudgetWithCoaAccounts(@Param("budget") Budget budget);

    Organisation findTopByBudgetOrderByIdDesc(Budget budget);

    Organisation findTopByOrderByIdDesc();

    @Query("""
    SELECT COALESCE(SUM(
        COALESCE(b.jan, 0) + COALESCE(b.feb, 0) + COALESCE(b.mar, 0) + 
        COALESCE(b.apr, 0) + COALESCE(b.may, 0) + COALESCE(b.jun, 0) +
        COALESCE(b.jul, 0) + COALESCE(b.aug, 0) + COALESCE(b.sep, 0) +
        COALESCE(b.oct, 0) + COALESCE(b.nov, 0) + COALESCE(b.dec, 0)
    ), 0)
    FROM BudgetItems b
    WHERE b.budget = :budget
    AND b.budgetType = :organisation
    AND b.coacode.code LIKE '1%'
    AND LENGTH(b.coacode.code) <= 6
""")
    BigDecimal sumAnnualByBudgetAndOrganisation(
            @Param("budget") Budget budget,
            @Param("organisation") Organisation organisation
    );

    // ===================================================================
    // BASIC FINDER METHODS
    // ===================================================================
    // Find by code
    Optional<Organisation> findByCode(String code);

    // Find by name
    List<Organisation> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    // Find by code containing
    List<Organisation> findByCodeContainingIgnoreCaseOrderByCodeAsc(String code);

    // Find by exact name
    Optional<Organisation> findByName(String name);

    // ===================================================================
    // BUDGET-RELATED QUERIES
    // ===================================================================
    // Find by budget
    List<Organisation> findByBudgetOrderByNameAsc(Budget budget);

    // Find by budget with COA count
    @Query("SELECT o, COUNT(c) FROM Organisation o LEFT JOIN o.coaAccounts c WHERE o.budget = :budget GROUP BY o ORDER BY o.name ASC")
    List<Object[]> findOrganisationsWithCOACountByBudget(@Param("budget") Budget budget);

    // Find by budget and name
    List<Organisation> findByBudgetAndNameContainingIgnoreCaseOrderByNameAsc(Budget budget, String name);

    // Find by budget and code
    List<Organisation> findByBudgetAndCodeContainingIgnoreCaseOrderByCodeAsc(Budget budget, String code);

    // ===================================================================
    // COA ASSIGNMENT QUERIES
    // ===================================================================
    // Find organisations with COA assignments
    @Query("SELECT DISTINCT o FROM Organisation o JOIN o.coaAccounts c ORDER BY o.name ASC")
    List<Organisation> findOrganisationsWithCOA();

    // Find organisations without COA assignments
    @Query("SELECT o FROM Organisation o WHERE o.coaAccounts IS EMPTY ORDER BY o.name ASC")
    List<Organisation> findOrganisationsWithoutCOA();

    // Find organisations with COA assignments by budget
    @Query("SELECT DISTINCT o FROM Organisation o JOIN o.coaAccounts c WHERE o.budget = :budget ORDER BY o.name ASC")
    List<Organisation> findOrganisationsWithCOAByBudget(@Param("budget") Budget budget);

    // Find organisations without COA assignments by budget
    @Query("SELECT o FROM Organisation o WHERE o.budget = :budget AND o.coaAccounts IS EMPTY ORDER BY o.name ASC")
    List<Organisation> findOrganisationsWithoutCOAByBudget(@Param("budget") Budget budget);

    // Find organisations with specific COA count
    @Query("SELECT o FROM Organisation o WHERE o.budget = :budget AND SIZE(o.coaAccounts) = :coaCount ORDER BY o.name ASC")
    List<Organisation> findOrganisationsWithCOACount(@Param("budget") Budget budget, @Param("coaCount") int coaCount);

    // Find organisations with COA count greater than
    @Query("SELECT o FROM Organisation o WHERE o.budget = :budget AND SIZE(o.coaAccounts) > :minCount ORDER BY o.name ASC")
    List<Organisation> findOrganisationsWithCOACountGreaterThan(@Param("budget") Budget budget, @Param("minCount") int minCount);

    // Find organisations with COA count less than
    @Query("SELECT o FROM Organisation o WHERE o.budget = :budget AND SIZE(o.coaAccounts) < :maxCount ORDER BY o.name ASC")
    List<Organisation> findOrganisationsWithCOACountLessThan(@Param("budget") Budget budget, @Param("maxCount") int maxCount);

    // ===================================================================
    // SEARCH QUERIES
    // ===================================================================
    // Search organisations by multiple criteria
    @Query("SELECT o FROM Organisation o WHERE "
            + "LOWER(o.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(o.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
            + "ORDER BY o.name ASC")
    Page<Organisation> searchOrganisations(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Search organisations by budget
    @Query("SELECT o FROM Organisation o WHERE o.budget = :budget AND ("
            + "LOWER(o.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(o.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) "
            + "ORDER BY o.name ASC")
    Page<Organisation> searchOrganisationsByBudget(@Param("budget") Budget budget, @Param("searchTerm") String searchTerm, Pageable pageable);

    // Advanced search with multiple criteria
    @Query("SELECT o FROM Organisation o WHERE "
            + "(:budget IS NULL OR o.budget = :budget) AND "
            + "(:searchTerm IS NULL OR "
            + "LOWER(o.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(o.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) "
            + "ORDER BY o.name ASC")
    List<Organisation> findByCriteria(@Param("budget") Budget budget, @Param("searchTerm") String searchTerm);

    // ===================================================================
    // COUNT QUERIES
    // ===================================================================
    // Count organisations with COA
    @Query("SELECT COUNT(DISTINCT o) FROM Organisation o JOIN o.coaAccounts c")
    Long countOrganisationsWithCOA();

    // Count organisations without COA
    @Query("SELECT COUNT(o) FROM Organisation o WHERE o.coaAccounts IS EMPTY")
    Long countOrganisationsWithoutCOA();

    // Count organisations by budget
    Long countByBudget(Budget budget);

    // Count organisations with COA by budget
    @Query("SELECT COUNT(DISTINCT o) FROM Organisation o JOIN o.coaAccounts c WHERE o.budget = :budget")
    Long countOrganisationsWithCOAByBudget(@Param("budget") Budget budget);

    // Count organisations without COA by budget
    @Query("SELECT COUNT(o) FROM Organisation o WHERE o.budget = :budget AND o.coaAccounts IS EMPTY")
    Long countOrganisationsWithoutCOAByBudget(@Param("budget") Budget budget);

    // Count total COA assignments
    @Query("SELECT COUNT(c) FROM COA c WHERE c.organisation IS NOT NULL")
    Long countTotalCOAAssignments();

    // Count COA assignments by budget
    @Query("SELECT COUNT(c) FROM COA c WHERE c.organisation IS NOT NULL AND c.budget = :budget")
    Long countCOAAssignmentsByBudget(@Param("budget") Budget budget);

    // ===================================================================
    // STATISTICAL QUERIES
    // ===================================================================
    // Count COA assignments by organisation
    @Query("SELECT o.name, COUNT(c) FROM Organisation o LEFT JOIN o.coaAccounts c GROUP BY o.id, o.name ORDER BY COUNT(c) DESC")
    List<Object[]> countCOAAssignmentsByOrganisation();

    // Count COA assignments by organisation for specific budget
    @Query("SELECT o.name, COUNT(c) FROM Organisation o LEFT JOIN o.coaAccounts c WHERE o.budget = :budget GROUP BY o.id, o.name ORDER BY COUNT(c) DESC")
    List<Object[]> countCOAAssignmentsByOrganisationAndBudget(@Param("budget") Budget budget);

    // Get organisation assignment statistics
    @Query("SELECT "
            + "COUNT(o) as totalOrganisations, "
            + "COUNT(CASE WHEN SIZE(o.coaAccounts) > 0 THEN 1 END) as organisationsWithCOA, "
            + "COUNT(CASE WHEN SIZE(o.coaAccounts) = 0 THEN 1 END) as organisationsWithoutCOA, "
            + "AVG(SIZE(o.coaAccounts)) as avgCOAPerOrganisation "
            + "FROM Organisation o WHERE o.budget = :budget")
    Object[] getOrganisationStatistics(@Param("budget") Budget budget);

    // Get COA distribution by organisation
    @Query("SELECT o.name, o.code, SIZE(o.coaAccounts) as coaCount FROM Organisation o WHERE o.budget = :budget ORDER BY SIZE(o.coaAccounts) DESC")
    List<Object[]> getCOADistributionByOrganisation(@Param("budget") Budget budget);

    // ===================================================================
    // RANKING QUERIES
    // ===================================================================
    // Find organisations with most COA assignments
    @Query("SELECT o FROM Organisation o LEFT JOIN o.coaAccounts c GROUP BY o ORDER BY COUNT(c) DESC")
    List<Organisation> findOrganisationsWithMostCOA();

    // Find organisations with least COA assignments
    @Query("SELECT o FROM Organisation o LEFT JOIN o.coaAccounts c GROUP BY o ORDER BY COUNT(c) ASC")
    List<Organisation> findOrganisationsWithLeastCOA(Pageable pageable);

    // Find top organisations by COA count for budget
    @Query("SELECT o FROM Organisation o LEFT JOIN o.coaAccounts c WHERE o.budget = :budget GROUP BY o ORDER BY COUNT(c) DESC")
    List<Organisation> findTopOrganisationsByBudget(@Param("budget") Budget budget, Pageable pageable);

    // ===================================================================
    // VALIDATION QUERIES
    // ===================================================================
    // Check if code exists
    boolean existsByCode(String code);

    // Check if name exists
    boolean existsByName(String name);

    // Check if code exists for different organisation
    @Query("SELECT COUNT(o) > 0 FROM Organisation o WHERE o.code = :code AND o.id != :id")
    boolean existsByCodeAndIdNot(@Param("code") String code, @Param("id") Long id);

    // Check if name exists for different organisation
    @Query("SELECT COUNT(o) > 0 FROM Organisation o WHERE o.name = :name AND o.id != :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Long id);

    // Find duplicate codes
    @Query("SELECT o.code, COUNT(o) FROM Organisation o GROUP BY o.code HAVING COUNT(o) > 1")
    List<Object[]> findDuplicateCodes();

    // Find duplicate names
    @Query("SELECT o.name, COUNT(o) FROM Organisation o GROUP BY o.name HAVING COUNT(o) > 1")
    List<Object[]> findDuplicateNames();

    // ===================================================================
    // COA-RELATED QUERIES
    // ===================================================================
    // Find unassigned COA for budget
    @Query("SELECT c FROM COA c WHERE c.budget = :budget AND c.organisation IS NULL ORDER BY c.code ASC")
    List<COA> findUnassignedCOAForBudget(@Param("budget") Budget budget);

    // Find assigned COA for organisation
    @Query("SELECT c FROM COA c WHERE c.organisation.id = :organisationId ORDER BY c.code ASC")
    List<COA> findAssignedCOAForOrganisation(@Param("organisationId") Long organisationId);

    // Find available COA for assignment (active and unassigned)
    @Query("SELECT c FROM COA c WHERE c.budget = :budget AND c.organisation IS NULL AND c.stateOpen = true ORDER BY c.code ASC")
    List<COA> findAvailableCOAForBudget(@Param("budget") Budget budget);

    // Find COA assigned to organisation by budget
    @Query("SELECT c FROM COA c WHERE c.organisation.id = :organisationId AND c.budget = :budget ORDER BY c.code ASC")
    List<COA> findCOAByOrganisationAndBudget(@Param("organisationId") Long organisationId, @Param("budget") Budget budget);

    // ===================================================================
    // REPORTING QUERIES
    // ===================================================================
    // Organisation utilization report
    @Query("SELECT "
            + "o.name as organisationName, "
            + "o.code as organisationCode, "
            + "o.budget.financialYear as budgetYear, "
            + "COUNT(c) as totalCOA, "
            + "SUM(CASE WHEN c.stateOpen = true THEN 1 ELSE 0 END) as activeCOA "
            + "FROM Organisation o LEFT JOIN o.coaAccounts c "
            + "GROUP BY o.id, o.name, o.code, o.budget.financialYear "
            + "ORDER BY o.name ASC")
    List<Object[]> getOrganisationUtilizationReport();

    // Organisation utilization report by budget
    @Query("SELECT "
            + "o.name as organisationName, "
            + "o.code as organisationCode, "
            + "COUNT(c) as totalCOA, "
            + "SUM(CASE WHEN c.stateOpen = true THEN 1 ELSE 0 END) as activeCOA "
            + "FROM Organisation o LEFT JOIN o.coaAccounts c "
            + "WHERE o.budget = :budget "
            + "GROUP BY o.id, o.name, o.code "
            + "ORDER BY COUNT(c) DESC")
    List<Object[]> getOrganisationUtilizationReportByBudget(@Param("budget") Budget budget);

    // COA assignment summary
    @Query("SELECT "
            + "COUNT(DISTINCT o) as totalOrganisations, "
            + "COUNT(DISTINCT CASE WHEN SIZE(o.coaAccounts) > 0 THEN o.id END) as organisationsWithCOA, "
            + "COUNT(DISTINCT CASE WHEN SIZE(o.coaAccounts) = 0 THEN o.id END) as organisationsWithoutCOA, "
            + "SUM(SIZE(o.coaAccounts)) as totalCOAAssignments "
            + "FROM Organisation o WHERE o.budget = :budget")
    Object[] getCOAAssignmentSummary(@Param("budget") Budget budget);

    // ===================================================================
    // MAINTENANCE QUERIES
    // ===================================================================
    // Find organisations without budget
    @Query("SELECT o FROM Organisation o WHERE o.budget IS NULL ORDER BY o.name ASC")
    List<Organisation> findOrganisationsWithoutBudget();

    // Find organisations with inactive COA
    @Query("SELECT DISTINCT o FROM Organisation o JOIN o.coaAccounts c WHERE c.stateOpen = false ORDER BY o.name ASC")
    List<Organisation> findOrganisationsWithInactiveCOA();

    // Find organisations with only inactive COA
    @Query("SELECT o FROM Organisation o WHERE "
            + "SIZE(o.coaAccounts) > 0 AND "
            + "NOT EXISTS (SELECT c FROM COA c WHERE c.organisation = o AND c.stateOpen = true) "
            + "ORDER BY o.name ASC")
    List<Organisation> findOrganisationsWithOnlyInactiveCOA();

    // Find organisations with mixed COA states
    @Query("SELECT o FROM Organisation o WHERE "
            + "EXISTS (SELECT c FROM COA c WHERE c.organisation = o AND c.stateOpen = true) AND "
            + "EXISTS (SELECT c FROM COA c WHERE c.organisation = o AND c.stateOpen = false) "
            + "ORDER BY o.name ASC")
    List<Organisation> findOrganisationsWithMixedCOAStates();

    // ===================================================================
    // PERFORMANCE QUERIES
    // ===================================================================
    // Find organisations with minimal data for dropdowns
    @Query("SELECT o FROM Organisation o WHERE o.budget = :budget ORDER BY o.name ASC")
    List<Organisation> findMinimalOrganisationsForDropdown(@Param("budget") Budget budget);

    // Find organisation names only
    @Query("SELECT o.name FROM Organisation o WHERE o.budget = :budget ORDER BY o.name ASC")
    List<String> findOrganisationNamesByBudget(@Param("budget") Budget budget);

    // Find organisation codes only
    @Query("SELECT o.code FROM Organisation o WHERE o.budget = :budget ORDER BY o.code ASC")
    List<String> findOrganisationCodesByBudget(@Param("budget") Budget budget);

    // ===================================================================
    // EXPORT QUERIES
    // ===================================================================
    // Export organisations with COA details
    @Query("SELECT o FROM Organisation o "
            + "LEFT JOIN FETCH o.coaAccounts "
            + "LEFT JOIN FETCH o.budget "
            + "WHERE o.budget = :budget "
            + "ORDER BY o.name ASC")
    List<Organisation> findForExport(@Param("budget") Budget budget);

    // Export organisations with COA assignments only
    @Query("SELECT o FROM Organisation o "
            + "LEFT JOIN FETCH o.coaAccounts c "
            + "LEFT JOIN FETCH o.budget "
            + "WHERE o.budget = :budget AND SIZE(o.coaAccounts) > 0 "
            + "ORDER BY o.name ASC")
    List<Organisation> findAssignedForExport(@Param("budget") Budget budget);

    // Export organisations without COA assignments
    @Query("SELECT o FROM Organisation o "
            + "LEFT JOIN FETCH o.budget "
            + "WHERE o.budget = :budget AND o.coaAccounts IS EMPTY "
            + "ORDER BY o.name ASC")
    List<Organisation> findUnassignedForExport(@Param("budget") Budget budget);

    // ===================================================================
    // COMPLEX FILTERING QUERIES
    // ===================================================================
    // Find organisations by multiple criteria with pagination
    @Query("SELECT o FROM Organisation o WHERE "
            + "(:budget IS NULL OR o.budget = :budget) AND "
            + "(:hasCoaAssignments IS NULL OR "
            + "(:hasCoaAssignments = true AND SIZE(o.coaAccounts) > 0) OR "
            + "(:hasCoaAssignments = false AND SIZE(o.coaAccounts) = 0)) AND "
            + "(:searchTerm IS NULL OR "
            + "LOWER(o.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(o.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) "
            + "ORDER BY o.name ASC")
    Page<Organisation> findByMultipleCriteria(@Param("budget") Budget budget,
            @Param("hasCoaAssignments") Boolean hasCoaAssignments,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    // Find organisations with COA count range
    @Query("SELECT o FROM Organisation o WHERE "
            + "o.budget = :budget AND "
            + "SIZE(o.coaAccounts) BETWEEN :minCount AND :maxCount "
            + "ORDER BY SIZE(o.coaAccounts) DESC")
    List<Organisation> findOrganisationsWithCOACountRange(@Param("budget") Budget budget,
            @Param("minCount") int minCount,
            @Param("maxCount") int maxCount);

    // ===================================================================
    // AUDIT QUERIES
    // ===================================================================
    // Find recently created organisations
    @Query("SELECT o FROM Organisation o WHERE o.budget = :budget ORDER BY o.id DESC")
    List<Organisation> findRecentlyCreated(@Param("budget") Budget budget, Pageable pageable);

    // Find organisations by name pattern
    @Query("SELECT o FROM Organisation o WHERE o.name LIKE :pattern ORDER BY o.name ASC")
    List<Organisation> findByNamePattern(@Param("pattern") String pattern);

    // Find organisations by code pattern
    @Query("SELECT o FROM Organisation o WHERE o.code LIKE :pattern ORDER BY o.code ASC")
    List<Organisation> findByCodePattern(@Param("pattern") String pattern);

    // ===================================================================
    // DASHBOARD STATISTICS
    // ===================================================================
    // Get comprehensive organisation statistics
    @Query("SELECT "
            + "COUNT(o) as totalOrganisations, "
            + "COUNT(CASE WHEN SIZE(o.coaAccounts) > 0 THEN 1 END) as organisationsWithCOA, "
            + "COUNT(CASE WHEN SIZE(o.coaAccounts) = 0 THEN 1 END) as organisationsWithoutCOA, "
            + "SUM(SIZE(o.coaAccounts)) as totalCOAAssignments, "
            + "AVG(SIZE(o.coaAccounts)) as avgCOAPerOrganisation, "
            + "MAX(SIZE(o.coaAccounts)) as maxCOAPerOrganisation "
            + "FROM Organisation o WHERE o.budget = :budget")
    Object[] getComprehensiveStatistics(@Param("budget") Budget budget);

    // Get monthly organisation creation stats
    /*    @Query("SELECT "
    + "YEAR(o.id) as year, "
    + "MONTH(o.id) as month, "
    + "COUNT(o) as organisationsCreated "
    + "FROM Organisation o "
    + "WHERE o.budget = :budget "
    + "GROUP BY YEAR(o.id), MONTH(o.id) "
    + "ORDER BY YEAR(o.id) DESC, MONTH(o.id) DESC")
    List<Object[]> getMonthlyCreationStats(@Param("budget") Budget budget);*/

    // ===================================================================
    // BUSINESS LOGIC QUERIES
    // ===================================================================
    // Find organisations eligible for COA assignment
    @Query("SELECT o FROM Organisation o WHERE "
            + "o.budget = :budget AND "
            + "EXISTS (SELECT c FROM COA c WHERE c.budget = :budget AND c.organisation IS NULL AND c.stateOpen = true) "
            + "ORDER BY o.name ASC")
    List<Organisation> findEligibleForCOAAssignment(@Param("budget") Budget budget);

    // Find organisations with unbalanced COA assignments
    @Query("SELECT o FROM Organisation o WHERE "
            + "o.budget = :budget AND "
            + "SIZE(o.coaAccounts) > 0 AND "
            + "(SIZE(o.coaAccounts) < :minThreshold OR SIZE(o.coaAccounts) > :maxThreshold) "
            + "ORDER BY SIZE(o.coaAccounts) DESC")
    List<Organisation> findOrganisationsWithUnbalancedCOA(@Param("budget") Budget budget,
            @Param("minThreshold") int minThreshold,
            @Param("maxThreshold") int maxThreshold);

    // Find organisations requiring attention (no COA or too many COA)
    @Query("SELECT o FROM Organisation o WHERE "
            + "o.budget = :budget AND "
            + "(SIZE(o.coaAccounts) = 0 OR SIZE(o.coaAccounts) > :threshold) "
            + "ORDER BY SIZE(o.coaAccounts) DESC")
    List<Organisation> findOrganisationsRequiringAttention(@Param("budget") Budget budget, @Param("threshold") int threshold);

    // ===================================================================
    // HIERARCHY QUERIES
    // ===================================================================
    // Find organisations with COA from specific hierarchy level
    @Query("SELECT DISTINCT o FROM Organisation o JOIN o.coaAccounts c WHERE "
            + "c.budget = :budget AND "
            + "(:coalevel1Id IS NULL OR c.coalevel1.id = :coalevel1Id) AND "
            + "(:coalevel11Id IS NULL OR c.coalevel11.id = :coalevel11Id) "
            + "ORDER BY o.name ASC")
    List<Organisation> findOrganisationsWithCOAFromHierarchy(@Param("budget") Budget budget,
            @Param("coalevel1Id") Long coalevel1Id,
            @Param("coalevel11Id") Long coalevel11Id);

    // Count organisations by COA hierarchy level
    @Query("SELECT cl1.name, COUNT(DISTINCT o) FROM Organisation o "
            + "JOIN o.coaAccounts c "
            + "JOIN c.coalevel1 cl1 "
            + "WHERE o.budget = :budget "
            + "GROUP BY cl1.id, cl1.name "
            + "ORDER BY COUNT(DISTINCT o) DESC")
    List<Object[]> countOrganisationsByHierarchyLevel(@Param("budget") Budget budget);

    // ===================================================================
    // BULK OPERATION SUPPORT
    // ===================================================================
    // Find organisations for bulk operations
    @Query("SELECT o FROM Organisation o WHERE o.id IN :organisationIds ORDER BY o.name ASC")
    List<Organisation> findForBulkOperations(@Param("organisationIds") List<Long> organisationIds);

    // Find organisations by codes for bulk operations
    @Query("SELECT o FROM Organisation o WHERE o.code IN :codes ORDER BY o.name ASC")
    List<Organisation> findByCodesForBulkOperations(@Param("codes") List<String> codes);

    // ===================================================================
    // INTEGRATION QUERIES
    // ===================================================================
    // Find organisations with specific COA codes
    @Query("SELECT DISTINCT o FROM Organisation o JOIN o.coaAccounts c WHERE "
            + "c.code IN :coaCodes AND o.budget = :budget ORDER BY o.name ASC")
    List<Organisation> findOrganisationsWithCOACodes(@Param("budget") Budget budget, @Param("coaCodes") List<String> coaCodes);

    // Find organisations missing specific COA types
    @Query("SELECT o FROM Organisation o WHERE "
            + "o.budget = :budget AND "
            + "NOT EXISTS (SELECT c FROM COA c WHERE c.organisation = o AND c.procclass = :procClass) "
            + "ORDER BY o.name ASC")
    List<Organisation> findOrganisationsMissingCOAType(@Param("budget") Budget budget, @Param("procClass") String procClass);

    // ===================================================================
    // CLEANUP QUERIES
    // ===================================================================
    // Find orphaned organisations (no budget)
    @Query("SELECT o FROM Organisation o WHERE o.budget IS NULL ORDER BY o.name ASC")
    List<Organisation> findOrphanedOrganisations();

    // Find empty organisations (no COA and no budget)
    @Query("SELECT o FROM Organisation o WHERE o.budget IS NULL AND o.coaAccounts IS EMPTY ORDER BY o.name ASC")
    List<Organisation> findEmptyOrganisations();

    // Find organisations with inconsistent data
    @Query("SELECT o FROM Organisation o WHERE "
            + "(o.name IS NULL OR o.name = '') OR "
            + "(o.code IS NULL OR o.code = '') "
            + "ORDER BY o.id ASC")
    List<Organisation> findOrganisationsWithInconsistentData();
}

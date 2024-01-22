package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Coalevel11;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.Section;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import java.util.List;
import java.util.Set;
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

    boolean existsByCode(String code);

    boolean existsByCodeAndBudget(String code, Budget budget);

    boolean existsByBudget(Budget budget);

    boolean existsBy();

    List<COA> findByBudget(Budget oldbudget);

    //COA findByCodeAndBudget(String code, Budget budget);
    @Query("SELECT c FROM COA c LEFT JOIN FETCH c.dsections WHERE c.code = :code AND c.budget = :budget")
    COA findByCodeAndBudgetWithDSections(@Param("code") String code, @Param("budget") Budget budget);

    @Query("SELECT c FROM COA c JOIN c.dsections ds WHERE c.budget = :budget")
    List<COA> findByBudgetAndDSections(@Param("budget") Budget budget);

    @Query("SELECT c FROM COA c JOIN c.deptsection dept WHERE dept IN :deptsection AND c.code LIKE :codePrefix% AND c.budget = :budget")
    List<COA> findByDeptsectionAndCodePrefix(@Param("deptsection") Set<UrcDeptSectionAnlDimbgt> deptsection, @Param("codePrefix") String codePrefix, @Param("budget") Budget budget);

    @Query("SELECT c FROM COA c JOIN c.deptsection dept WHERE dept IN :deptsection AND c.code LIKE :codePrefix% AND ((LOWER(c.code) LIKE LOWER(CONCAT('%', :category, '%')) OR LOWER(c.name) LIKE LOWER(CONCAT('%', :category, '%'))) OR (c.name IS NULL AND :category IS NULL)) AND c.budget = :budget")
    List<COA> findByDeptsectionAndCodePrefixSearch(@Param("deptsection") Set<UrcDeptSectionAnlDimbgt> deptsection, @Param("codePrefix") String codePrefix, @Param("category") String category, @Param("budget") Budget budget);

    @Query("SELECT c FROM COA c "
            + "JOIN c.deptsection dept "
            + "WHERE dept IN :deptsection "
            + "AND c.coalevel1 = :coalevel1 "
            + "AND c.budget = :budget "
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
            + "AND c.budget = :budget")
    List<COA> findBySectionsAndCodePrefixSearch2(
            @Param("sections") Set<Section> sections,
            @Param("codePrefix") String codePrefix,
            @Param("category") String category,
            @Param("budget") Budget budget
    );

    @Query("SELECT c FROM COA c "
            + "JOIN c.deptsection ds "
            + "WHERE ds = :deptSection AND c.code LIKE :codeFilter% AND c.budget = :budget")
    List<COA> findByDeptSectionAndCodeStartingWith(
            @Param("deptSection") UrcDeptSectionAnlDimbgt deptSection,
            @Param("codeFilter") String codeFilter,
            @Param("budget") Budget budget
    );

    List<COA> findByBudgetAndDisplay(Budget budget, Display display);

    @Query("SELECT c FROM COA c WHERE c.code = :code AND c.budget = :budget")
    COA findByCodeAndBudget(@Param("code") String code, @Param("budget") Budget budget);

    List<COA> findByBudgetAndDisplayAndStateOpen(Budget budget, Display display, boolean stateOpen);
}

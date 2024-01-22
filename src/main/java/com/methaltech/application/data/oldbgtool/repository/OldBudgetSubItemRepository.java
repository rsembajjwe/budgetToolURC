package com.methaltech.application.data.oldbgtool.repository;

import com.methaltech.application.data.entity.oldbgtool.OldBudgetSubItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OldBudgetSubItemRepository extends JpaRepository<OldBudgetSubItem, Long> {

    List<OldBudgetSubItem> findBySectAndFy(Integer sect, String fy);

    @Query("SELECT o.deptunit FROM OldBudgetSubItem o WHERE o.fy = :fy AND o.progactivity = :progactivity")
    List<Integer> findDistinctSectByFyAndProgactivity(@Param("fy") String fy, @Param("progactivity") Integer progactivity);

    @Query("SELECT o.deptunit FROM OldBudgetSubItem o WHERE o.fy = :fy AND o.progactivity = :progactivity")
    List<Integer> findDeptunitByFyAndProgactivity(String fy, Integer progactivity);

    List<OldBudgetSubItem> findAllByFyAndProgactivity(String fy, Integer progactivity);
    List<OldBudgetSubItem> findAllByProgactivity(Integer progactivity);

    @Query("SELECT o.deptunit FROM OldBudgetSubItem o WHERE o.progactivity = :progactivity")
    List<Integer> findDeptunitsByProgactivity(@Param("progactivity") Integer progactivity);
    
    List<OldBudgetSubItem> findByFy(String fy);
}

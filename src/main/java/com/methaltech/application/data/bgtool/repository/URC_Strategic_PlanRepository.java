package com.methaltech.application.data.bgtool.repository;


import com.methaltech.application.data.entity.bgtool.National_Budget_Focus_Areas;
import com.methaltech.application.data.entity.bgtool.URC_Strategic_Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface URC_Strategic_PlanRepository extends JpaRepository<URC_Strategic_Plan, Long> {

    Page<URC_Strategic_Plan> findByNationalBudgetFocusArea(National_Budget_Focus_Areas ndp111Objective, Pageable pageable);
}

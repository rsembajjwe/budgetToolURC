package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.URC_Priority_Areas;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.bgtool.repository.Urc_ActivitiesRepository;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.StaffSalary;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Urc_ActivitiesService {

    private final Urc_ActivitiesRepository repository;

    @Autowired
    public Urc_ActivitiesService(Urc_ActivitiesRepository repository) {
        this.repository = repository;
    }

    public Optional<Urc_Activities> get(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Urc_Activities update(Urc_Activities entity) {
        return repository.save(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
    @Transactional
    public void deleteByActivity(Urc_Activities entity) {
        repository.deleteActivity(entity);
    }

    public Page<Urc_Activities> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public Page listByUrcPriorityAreas(URC_Priority_Areas value, Pageable pageable) {
        return repository.findByUrcPriorityAreas(value, pageable);
    }

    public List<Urc_Activities> listByBudget(Budget budget) {
        return repository.findByBudget2(budget);
    }

    public List<Urc_Activities> listByBudget2(Budget budget) {
        return repository.findByBudget(budget);
    }

    public List<Urc_Activities> findActivitiesByBudgetAndPriorityAndDeptUnits(Budget budget, URC_Priority_Areas urcPriorityAreas, List<UrcDeptSectionAnlDimbgt> deptUnits) {
        return repository.findByBudgetAndUrcPriorityAreasAndDeptSectionIn(budget, urcPriorityAreas, deptUnits);
    }

    public List<Urc_Activities> findActivitiesByPriorityAreas(URC_Priority_Areas urcPriorityAreas) {
        return repository.findByUrcPriorityAreas(urcPriorityAreas);
    }

    public List<Urc_Activities> findByUrcPriorityAreas(URC_Priority_Areas urcPriorityAreas) {
        return repository.findByUrcPriorityAreas(urcPriorityAreas);
    }

    public List<Urc_Activities> findByUrcPriorityAreasAndBudgetAndDeptSectionIn(URC_Priority_Areas urcPriorityAreas, Budget budget, List<UrcDeptSectionAnlDimbgt> deptSections) {
        return repository.findByUrcPriorityAreasAndBudgetAndDeptSectionIn(urcPriorityAreas, budget, deptSections);
    }

    public List<Urc_Activities> findByBudgetAndSearch(Budget budget, String search) {
        return repository.findByBudgetAndSearch(budget, search);
    }

    public List<Urc_Activities> findByDeptSectionAndBudget(UrcDeptSectionAnlDimbgt deptSection, Budget budget, String filter) {
        return repository.findByDeptSectionAndBudgetAndNameContaining(deptSection, budget, filter);
    }

    public List<Urc_Activities> findByDeptSectionAndBudget(UrcDeptSectionAnlDimbgt deptSection, Budget budget) {
        return repository.findByDeptSectionAndBudget(deptSection, budget);
    }

    public List<Urc_Activities> findByDeptSectionAndBudgetAndSearch(UrcDeptSectionAnlDimbgt deptSection, Budget budget, String filter) {
        return repository.findByDeptSectionAndBudgetAndSearch(deptSection, budget, filter);
    }

    public Urc_Activities createUrcActivity(Urc_Activities activity, String anlCode) {
        // Find the maximum existing activityCode for the given ANL_CODE
        String maxActivityCode = repository.findMaxActivityCodeByAnlCode(anlCode.trim());

        // Calculate the next activityCode (e.g., increment by 1)
        String nextActivityCode = "";
        if (maxActivityCode != null) {
            int nextActivityCodeNumber = Integer.parseInt(maxActivityCode.substring(4)) + 1;
            nextActivityCode = anlCode.trim() + nextActivityCodeNumber;
        } else {
            nextActivityCode = anlCode.trim() + "1";
            //System.out.println(anlCode.trim() + "1");
        }

        activity.setActivityCode(nextActivityCode);
        return repository.save(activity);
    }

    public String maxActivityCode(String anlCode) {
        return repository.findMaxActivityCodeByAnlCode(anlCode.trim());
    }

    public Urc_Activities getLastSavedUrcActivities() {
        return repository.findTopByOrderByIdDesc();
    }
    public Optional<Urc_Activities> findUrcActivityById(Long id) {
        return repository.findById(id);
    }

    public List<Urc_Activities> findUrcActivitiesByOrigid(Long origid) {
        return repository.findByOrigid(origid);
    }

    public List<Urc_Activities> findByBudgetAndDeptSectionIn(Budget budget, List<UrcDeptSectionAnlDimbgt> deptSections) {
        return repository.findByBudgetAndDeptSectionIn(budget, deptSections);
    }

    public List<Urc_Activities> customSearchByFields(Budget budget, URC_Priority_Areas urcPriorityAreas, List<UrcDeptSectionAnlDimbgt> deptSections, String keyword) {
        return repository.customSearchByFields(budget, urcPriorityAreas, deptSections, keyword);
    }

    public Urc_Activities findByActivityCodeAndBudget(String keyword, Budget budget) {
        return repository.findByActivityCodeAndBudget(keyword, budget);
    }
}

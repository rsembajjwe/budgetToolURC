package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.URC_Priority_Areas;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.bgtool.repository.Urc_ActivitiesRepository;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.StaffSalary;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    /*    public List<Urc_Activities> listByBudget(Budget budget) {
    return repository.findByBudget2(budget);
    }*/
    public List<Urc_Activities> listByBudget2(Budget budget) {
        return repository.findByBudget(budget);
    }

    public List<Urc_Activities> findActivitiesByBudgetAndPriorityAndDeptUnits(Budget budget, URC_Priority_Areas urcPriorityAreas, List<UrcDeptSectionAnlDimbgt> deptUnits) {
        return repository.findByBudgetAndUrcPriorityAreasAndDeptSectionIn(budget, urcPriorityAreas, deptUnits);
    }

    @Transactional(readOnly = true)
    public List<Urc_Activities> getLoadedActivities(Budget budget,
            URC_Priority_Areas priority,
            List<UrcDeptSectionAnlDimbgt> sections) {
        return repository.findWithAllJoins(budget, priority, sections);
    }

    public List<Urc_Activities> findActivitiesByPriorityAreas(URC_Priority_Areas urcPriorityAreas) {
        return repository.findByUrcPriorityAreas(urcPriorityAreas);
    }

    public List<Urc_Activities> findWithAllJoinsByBudgetAndSectionSet(Budget budget, Set<UrcDeptSectionAnlDimbgt> sections) {
        return repository.findWithAllJoinsByBudgetAndSectionSet(budget, sections);
    }

    public List<Urc_Activities> findByUrcPriorityAreas(URC_Priority_Areas urcPriorityAreas) {
        return repository.findByUrcPriorityAreas(urcPriorityAreas);
    }

    public List<Urc_Activities> findByUrcPriorityAreasAndBudgetAndDeptSectionIn(URC_Priority_Areas urcPriorityAreas, Budget budget, List<UrcDeptSectionAnlDimbgt> deptSections) {
        return repository.findByUrcPriorityAreasAndBudgetAndDeptSectionIn(urcPriorityAreas, budget, deptSections);
    }

    /*    public List<Urc_Activities> findByBudgetAndSearch(Budget budget, String search) {
    return repository.findByBudgetAndSearch(budget, search);
    }*/
    public List<Urc_Activities> findByDeptSectionAndBudget(UrcDeptSectionAnlDimbgt deptSection, Budget budget, String filter) {
        return repository.findByDeptSectionAndBudgetAndNameContaining(deptSection, budget, filter);
    }

    public List<Urc_Activities> findByDeptSectionAndBudget(UrcDeptSectionAnlDimbgt deptSection, Budget budget) {
        return repository.findByDeptSectionAndBudget(deptSection, budget);
    }

    public List<Urc_Activities> findByDeptSectionAndBudget(Set<UrcDeptSectionAnlDimbgt> deptSection, Budget budget) {
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

    public List<Urc_Activities> getAllActivities() {
        return repository.findAll();
    }

    public Optional<Urc_Activities> getActivityById(Long id) {
        return repository.findById(id);
    }

    public List<Urc_Activities> getActivitiesBySection(UrcDeptSectionAnlDimbgt section) {
        return repository.findByDeptSectionOrderByActivityCodeAsc(section);
    }

    public List<Urc_Activities> getActivitiesByBudget(Budget budget) {
        return repository.findByBudgetOrderByActivityCodeAsc(budget);
    }

    public List<Urc_Activities> getActivitiesBySectionAndBudget(UrcDeptSectionAnlDimbgt section, Budget budget) {
        return repository.findByDeptSectionAndBudgetOrderByActivityCodeAsc(section, budget);
    }

    public List<Urc_Activities> getActivitiesByCode(String activityCode) {
        return repository.findByActivityCodeContainingIgnoreCaseOrderByActivityCodeAsc(activityCode);
    }

    public List<Urc_Activities> getActivitiesByName(String name) {
        return repository.findByNameContainingIgnoreCaseOrderByNameAsc(name);
    }

    public Page<Urc_Activities> searchActivities(String searchTerm, Pageable pageable) {
        return repository.searchActivities(searchTerm, pageable);
    }

    public List<Urc_Activities> getActivitiesWithValidBudget() {
        return repository.findActivitiesWithValidBudget();
    }

    public List<Urc_Activities> getActivitiesByFundSource(String fundSource) {
        return repository.findByFundsourceContainingIgnoreCaseOrderByActivityCodeAsc(fundSource);
    }

    @Transactional
    public Urc_Activities saveActivity(Urc_Activities activity) {
        return repository.save(activity);
    }

    @Transactional
    public List<Urc_Activities> saveAllActivities(List<Urc_Activities> activities) {

        if (activities == null || activities.isEmpty()) {
            return Collections.emptyList();
        }

        // Optional: Pre-validation / normalization
        activities.forEach(this::prepareForSave);

        return repository.saveAll(activities);
    }

    private void prepareForSave(Urc_Activities activity) {

        // Example safety checks
        if (activity.getActivity_budget() == null) {
            activity.setActivity_budget(BigDecimal.ZERO);
        }

        if (activity.getDeliverable_outputs() == null) {
            activity.setDeliverable_outputs(new HashSet<>());
        }

        // Maintain bidirectional relationship integrity
        if (activity.getQuarterlyActuals() != null) {
            activity.getQuarterlyActuals()
                    .forEach(q -> q.setActivity(activity));
        }
    }

    @Transactional
    public void deleteActivity(Long activityId) {
        repository.deleteById(activityId);
    }

    // Dashboard statistics
    public Long getTotalActivitiesCount() {
        return repository.count();
    }

    public Long getActivitiesCountBySection(UrcDeptSectionAnlDimbgt section) {
        return repository.countByDeptSection(section);
    }

    public Long getActivitiesCountByBudget(Budget budget) {
        return repository.countByBudget(budget);
    }

    public Double getTotalActivityBudget(Budget budget) {
        Double total = repository.sumActivityBudgetByBudget(budget);
        return total != null ? total : 0.0;
    }

    public Double getTotalActivityBudgetBySection(UrcDeptSectionAnlDimbgt section, Budget budget) {
        Double total = repository.sumActivityBudgetBySectionAndBudget(section, budget);
        return total != null ? total : 0.0;
    }

    public String getDistinctFundSources(Long budgetId) {
        String sources = repository.findDistinctFundSourcesByBudgetNative(budgetId);
        return sources != null ? sources : "Not Specified";
    }
}

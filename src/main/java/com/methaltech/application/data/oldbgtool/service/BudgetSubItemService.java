
package com.methaltech.application.data.oldbgtool.service;

import com.methaltech.application.data.entity.oldbgtool.BudgetSubItem;
import com.methaltech.application.data.oldbgtool.repository.BudgetSubItemRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BudgetSubItemService {

    private final BudgetSubItemRepository repository;

    public BudgetSubItemService(BudgetSubItemRepository repository) {
        this.repository = repository;
    }


    public BigDecimal getTotalByCategoryAndFiscalYearAndDeptUnits(String category, String fiscalYear, List<Integer> deptUnits) {
        return repository.getTotalByCategoryAndFiscalYearAndDeptUnits(category, fiscalYear, deptUnits);
    }

    public BigDecimal sumTotalByActivitiesByUnitByCategoryByFy(List<Integer> activityList, List<Integer> deptUnits, String bcategory, String fiscalYear) {
        return repository.sumTotalByActivitiesByUnitByCategoryByFy(activityList, deptUnits, bcategory, fiscalYear);
    }
    
    public BigDecimal sumTotalByActivityByUnitByCategoryByFy(Integer activityList, List<Integer> deptUnits, String bcategory, String fiscalYear) {
        return repository.sumTotalByActivityByUnitByCategoryByFy(activityList, deptUnits, bcategory, fiscalYear);
    } 
    public BigDecimal sumJulByActivityByUnitByCategoryByFy(Integer activityList, List<Integer> deptUnits, String bcategory, String fiscalYear) {
        return repository.sumJulByActivityByUnitByCategoryByFy(activityList, deptUnits, bcategory, fiscalYear);
    }
    public BigDecimal sumAugByActivityByUnitByCategoryByFy(Integer activityList, List<Integer> deptUnits, String bcategory, String fiscalYear) {
        return repository.sumAugByActivityByUnitByCategoryByFy(activityList, deptUnits, bcategory, fiscalYear);
    }
    public BigDecimal sumSepByActivityByUnitByCategoryByFy(Integer activityList, List<Integer> deptUnits, String bcategory, String fiscalYear) {
        return repository.sumSepByActivityByUnitByCategoryByFy(activityList, deptUnits, bcategory, fiscalYear);
    }
    public BigDecimal sumOctByActivityByUnitByCategoryByFy(Integer activityList, List<Integer> deptUnits, String bcategory, String fiscalYear) {
        return repository.sumOctByActivityByUnitByCategoryByFy(activityList, deptUnits, bcategory, fiscalYear);
    }
    public BigDecimal sumNovByActivityByUnitByCategoryByFy(Integer activityList, List<Integer> deptUnits, String bcategory, String fiscalYear) {
        return repository.sumNovByActivityByUnitByCategoryByFy(activityList, deptUnits, bcategory, fiscalYear);
    }
    public BigDecimal sumDecByActivityByUnitByCategoryByFy(Integer activityList, List<Integer> deptUnits, String bcategory, String fiscalYear) {
        return repository.sumDecByActivityByUnitByCategoryByFy(activityList, deptUnits, bcategory, fiscalYear);
    }
    public BigDecimal sumJanByActivityByUnitByCategoryByFy(Integer activityList, List<Integer> deptUnits, String bcategory, String fiscalYear) {
        return repository.sumJanByActivityByUnitByCategoryByFy(activityList, deptUnits, bcategory, fiscalYear);
    }
    public BigDecimal sumFebByActivityByUnitByCategoryByFy(Integer activityList, List<Integer> deptUnits, String bcategory, String fiscalYear) {
        return repository.sumFebByActivityByUnitByCategoryByFy(activityList, deptUnits, bcategory, fiscalYear);
    }
    public BigDecimal sumMarByActivityByUnitByCategoryByFy(Integer activityList, List<Integer> deptUnits, String bcategory, String fiscalYear) {
        return repository.sumMarByActivityByUnitByCategoryByFy(activityList, deptUnits, bcategory, fiscalYear);
    }
    public BigDecimal sumAprByActivityByUnitByCategoryByFy(Integer activityList, List<Integer> deptUnits, String bcategory, String fiscalYear) {
        return repository.sumAprByActivityByUnitByCategoryByFy(activityList, deptUnits, bcategory, fiscalYear);
    }
    public BigDecimal sumMayByActivityByUnitByCategoryByFy(Integer activityList, List<Integer> deptUnits, String bcategory, String fiscalYear) {
        return repository.sumMayByActivityByUnitByCategoryByFy(activityList, deptUnits, bcategory, fiscalYear);
    }
    public BigDecimal sumJunByActivityByUnitByCategoryByFy(Integer activityList, List<Integer> deptUnits, String bcategory, String fiscalYear) {
        return repository.sumJunByActivityByUnitByCategoryByFy(activityList, deptUnits, bcategory, fiscalYear);
    } 
    public BigDecimal sumMonthsByActivityByUnitByCategoryByFy(Integer activityList, List<Integer> deptUnits, String bcategory, String fiscalYear) {
        return repository.sumMonthsByActivityByUnitByCategoryByFy(activityList, deptUnits, bcategory, fiscalYear);
    } 
    
    public BigDecimal sumTotalByActivities(Integer activity) {
        return repository.sumTotalByActivities(activity);
    }  
    public BigDecimal sumTotalByActivitiesList(List<Integer> activity) {
        return repository.sumTotalByActivitiesList(activity);
    } 
    
    public List<BudgetSubItem> BudgetByActivitiesByUnitByCategoryByFy(Integer progActivityList,String bCategory,String fiscalYear, List<Integer> deptUnits) {
        return repository.BudgetByActivitiesByUnitByCategoryByFy(progActivityList,bCategory,fiscalYear,deptUnits);
    } 
    public List<BudgetSubItem> BudgetByUnitByCategoryByFy(String bCategory,String fiscalYear, List<Integer> deptUnits) {
        return repository.BudgetByUnitByCategoryByFy(bCategory,fiscalYear,deptUnits);
    }    
}

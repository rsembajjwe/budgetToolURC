
package com.methaltech.application.data.bgtool.service.report;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import java.util.List;
import java.util.Set;


public class BudgetReportRequest {

    private final Budget budget;
    private final List<UrcDeptSectionAnlDimbgt> selectedSections;
    private final Set<Organisation> selectedBudgetTypes;

    public BudgetReportRequest(Budget budget,
                               List<UrcDeptSectionAnlDimbgt> selectedSections,
                               Set<Organisation> selectedBudgetTypes) {
        this.budget = budget;
        this.selectedSections = selectedSections;
        this.selectedBudgetTypes = selectedBudgetTypes;
    }

    public Budget getBudget() {
        return budget;
    }

    public List<UrcDeptSectionAnlDimbgt> getSelectedSections() {
        return selectedSections;
    }

    public Set<Organisation> getSelectedBudgetTypes() {
        return selectedBudgetTypes;
    }
}

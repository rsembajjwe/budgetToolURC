package com.methaltech.application.data.entity.bgtool;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;

public class PerformanceReportContext {

    private Budget budget;
    private String financialYear;
    private int quarter;

    private Map<String, SectionBudgetPerformance> performanceMap;
    private List<PriorityArea> priorityAreas;
    private LocalDateTime generatedOn;

    private PerformanceReportContext() {
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public int getQuarter() {
        return quarter;
    }
    public Budget getBudget(){
        return budget;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final PerformanceReportContext ctx = new PerformanceReportContext();

        public Builder budget(Budget budget) {
            ctx.budget = budget;
            return this;
        }

        public Builder financialYear(String financialYear) {
            ctx.financialYear = financialYear;
            return this;
        }

        public Builder quarter(int quarter) {
            ctx.quarter = quarter;
            return this;
        }

        public Builder performanceMap(Map<String, SectionBudgetPerformance> map) {
            ctx.performanceMap = map;
            return this;
        }

        public Builder priorityAreas(List<PriorityArea> areas) {
            ctx.priorityAreas = areas;
            return this;
        }

        public Builder generatedOn(LocalDateTime time) {
            ctx.generatedOn = time;
            return this;
        }

        public PerformanceReportContext build() {
            return ctx;
        }
    }

    // getters only
}

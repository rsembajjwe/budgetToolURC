package com.methaltech.application.data.entity.bgtool.dto;

import java.math.BigDecimal;

public class PerformanceReportDTO {

    BigDecimal fuelExpense;
    BigDecimal passengerExpense;
    BigDecimal maintenanceExpense;
    BigDecimal utitlity_PropertyExpense;

    BigDecimal generalExpense;

    BigDecimal board_legalExpense;
    BigDecimal communicationsExpense;
    BigDecimal utilitiesExpense;
    BigDecimal supplies_servicesExpense;
    BigDecimal professional_servicesExpense;
    BigDecimal insurancce_licenseExpense;
    BigDecimal travel_transportExpense;
    BigDecimal miscellanous_otherExpense;


    public PerformanceReportDTO(BigDecimal fuelExpense, BigDecimal passengerExpense, BigDecimal maintenanceExpense, BigDecimal utitlity_PropertyExpense) {
        this.fuelExpense = fuelExpense;
        this.passengerExpense = passengerExpense;
        this.maintenanceExpense = maintenanceExpense;
        this.utitlity_PropertyExpense = utitlity_PropertyExpense;
    }

    public PerformanceReportDTO(BigDecimal generalExpense) {
        this.generalExpense = generalExpense;
    }

    public PerformanceReportDTO(BigDecimal board_legalExpense, BigDecimal communicationsExpense, BigDecimal utilitiesExpense, BigDecimal supplies_servicesExpense, BigDecimal professional_servicesExpense, BigDecimal insurancce_licenseExpense, BigDecimal travel_transportExpense, BigDecimal miscellanous_otherExpense) {
        this.board_legalExpense = board_legalExpense;
        this.communicationsExpense = communicationsExpense;
        this.utilitiesExpense = utilitiesExpense;
        this.supplies_servicesExpense = supplies_servicesExpense;
        this.professional_servicesExpense = professional_servicesExpense;
        this.insurancce_licenseExpense = insurancce_licenseExpense;
        this.travel_transportExpense = travel_transportExpense;
        this.miscellanous_otherExpense = miscellanous_otherExpense;
    }

    public BigDecimal getVariableExpense() {
        return fuelExpense.add(passengerExpense).add(maintenanceExpense).add(utitlity_PropertyExpense);
    }

    public BigDecimal getAdminExpense() {
        return generalExpense;
    }

    public BigDecimal getOtherAdminExpense() {
        return board_legalExpense.add(communicationsExpense).add(utilitiesExpense).add(supplies_servicesExpense)
                .add(professional_servicesExpense).add(insurancce_licenseExpense).add(travel_transportExpense).add(miscellanous_otherExpense);
    }

}

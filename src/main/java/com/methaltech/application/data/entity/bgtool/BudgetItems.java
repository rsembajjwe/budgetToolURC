package com.methaltech.application.data.entity.bgtool;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.salaryScale;
import jakarta.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.FetchType;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "budgetItem")
@NoArgsConstructor
@Getter
@Setter
public class BudgetItems implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String item;
    private String product;
    private String category;
    private Long analcode;
    private salaryScale grade;
    @Column(precision = 25, scale = 6)
    private BigDecimal cost;

    @Transient
    private Long syntheticGroupId;

    @Transient
    private Boolean syntheticGroupedRow = false;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    @JsonIgnore
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "fundsource_id")
    @JsonIgnore
    private Fundsource fundsource;

    @ManyToOne
    @JoinColumn(name = "coalevel1_id")
    private Coalevel1 coalevel1;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "activity_id2")
    private Urc_Activities activity;

    @ManyToOne
    @JoinColumn(name = "dsection_id")
    private UrcDeptSectionAnlDimbgt deptUnit;

    private int units;
    @Column(precision = 25, scale = 6)
    private BigDecimal qty;
    @Column(precision = 25, scale = 6)
    private BigDecimal jul;
    @Column(precision = 25, scale = 6)
    private BigDecimal nov;
    @Column(precision = 25, scale = 6)
    private BigDecimal mar;
    @Column(precision = 25, scale = 6)
    private BigDecimal aug;
    @Column(precision = 25, scale = 6)
    private BigDecimal dec;
    @Column(precision = 25, scale = 6)
    private BigDecimal apr;
    @Column(precision = 25, scale = 6)
    private BigDecimal sep;
    @Column(precision = 25, scale = 6)
    private BigDecimal jan;
    @Column(precision = 25, scale = 6)
    private BigDecimal may;
    @Column(precision = 25, scale = 6)
    private BigDecimal oct;
    @Column(precision = 25, scale = 6)
    private BigDecimal feb;
    @Column(precision = 25, scale = 6)
    private BigDecimal jun;
    private String type;
    @Column(precision = 25, scale = 6)
    private BigDecimal total;
    @ManyToOne
    @JoinColumn(name = "coa_id")
    private COA coacode;
    @Column(length = 255)
    private String notes;
    private String bcategory;

    private String unitMeasure;
    private String target_group;
    private String expected_trainer;
    private String no_of_days;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procurement_method_id")
    private ProcurementMethod procurementMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procurement_type_id")
    private ProcurementType procurementType;

    @Column(name = "prequalification")
    private Boolean prequalification = false;

    @Column(name = "reserve_scheme")
    private Boolean reserveScheme = false;

    @Column(name = "bid_invitation_date")
    private LocalDate bidInvitationDate;

    @Column(name = "bid_closing_opening_date")
    private LocalDate bidClosingOpeningDate;

    @Column(name = "evaluation_approval_date")
    private LocalDate evaluationApprovalDate;

    @Column(name = "award_notification_date")
    private LocalDate awardNotificationDate;

    @Column(name = "contract_signing_date")
    private LocalDate contractSigningDate;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @Column(name = "proposal_invitation_date")
    private LocalDate proposalInvitationDate;

    @Column(name = "proposal_submission_opening_date")
    private LocalDate proposalSubmissionOpeningDate;

    @Column(name = "final_evaluation_approval_date")
    private LocalDate finalEvaluationApprovalDate;

    @Column(name = "final_notification_date")
    private LocalDate finalNotificationDate;

    @Column(name = "current_year_estimated_cost", precision = 25, scale = 6)
    private BigDecimal currentYearEstimatedCost;

    @Column(name = "projected_completion_time_years")
    private Integer projectedCompletionTimeYears;

    @Column(name = "paid_up_sum", precision = 25, scale = 6)
    private BigDecimal paidUpSum;

    @Column(name = "pending_sum", precision = 25, scale = 6)
    private BigDecimal pendingSum;

    @Column(name = "pending_time_to_completion")
    private String pendingTimeToCompletion;

    @Column(name = "reservation_scheme_details")
    private String reservationSchemeDetails;

    @ManyToOne
    @JoinColumn(name = "Organisation_id")
    private Organisation budgetType;
    @Enumerated(EnumType.STRING)
    @Column(name = "procureClass_id")
    private ProcClass procClass;

    // Helper methods
    public String getDisplayName() {
        return item;
    }

    public double getTotalCost() {
        return total != null ? total.doubleValue() : 0.0;
    }

    public double getUnitCost() {
        return cost != null ? cost.doubleValue() : 0.0;
    }

    public String getAccountCode() {
        return coacode != null ? coacode.getCode() : "N/A";
    }

    public String getAccountName() {
        return coacode != null ? coacode.getName() : "N/A";
    }

    public String getProcurementClass() {
        return procClass != null ? procClass.name() : "Not Specified";
    }

    public boolean hasValidCost() {
        return cost != null && cost.compareTo(BigDecimal.ZERO) > 0;
    }

    public String getFormattedCost() {
        if (cost == null) {
            return "UGX 0";
        }
        return String.format("UGX %,.2f", cost.doubleValue());
    }

    public String getFormattedTotal() {
        BigDecimal tot = BigDecimal.ZERO;
        if (jul != null) {
            tot = tot.add(jul);
        }
        if (aug != null) {
            tot = tot.add(aug);
        }
        if (sep != null) {
            tot = tot.add(sep);
        }
        if (oct != null) {
            tot = tot.add(oct);
        }
        if (nov != null) {
            tot = tot.add(nov);
        }
        if (dec != null) {
            tot = tot.add(dec);
        }
        if (jan != null) {
            tot = tot.add(jan);
        }
        if (feb != null) {
            tot = tot.add(feb);
        }
        if (mar != null) {
            tot = tot.add(mar);
        }
        if (apr != null) {
            tot = tot.add(apr);
        }
        if (may != null) {
            tot = tot.add(may);
        }
        if (jun != null) {
            tot = tot.add(jun);
        }

        return String.format("UGX %,.2f", tot.doubleValue());
    }

    public double getCalculatedTotal() {
        BigDecimal tot = BigDecimal.ZERO;
        if (jul != null) {
            tot = tot.add(jul);
        }
        if (aug != null) {
            tot = tot.add(aug);
        }
        if (sep != null) {
            tot = tot.add(sep);
        }
        if (oct != null) {
            tot = tot.add(oct);
        }
        if (nov != null) {
            tot = tot.add(nov);
        }
        if (dec != null) {
            tot = tot.add(dec);
        }
        if (jan != null) {
            tot = tot.add(jan);
        }
        if (feb != null) {
            tot = tot.add(feb);
        }
        if (mar != null) {
            tot = tot.add(mar);
        }
        if (apr != null) {
            tot = tot.add(apr);
        }
        if (may != null) {
            tot = tot.add(may);
        }
        if (jun != null) {
            tot = tot.add(jun);
        }

        return tot.doubleValue();
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    public BigDecimal getQ1Total() { // Jul-Sep
        return nvl(jul).add(nvl(aug)).add(nvl(sep));
    }

    public BigDecimal getQ2Total() { // Oct-Dec
        return nvl(oct).add(nvl(nov)).add(nvl(dec));
    }

    public BigDecimal getQ3Total() { // Jan-Mar
        return nvl(jan).add(nvl(feb)).add(nvl(mar));
    }

    public BigDecimal getQ4Total() { // Apr-Jun
        return nvl(apr).add(nvl(may)).add(nvl(jun));
    }

    public BigDecimal getYearTotalFromQuarters() {
        return getQ1Total().add(getQ2Total()).add(getQ3Total()).add(getQ4Total());
    }

    @Transient
    public String getProcurementMethodName() {
        return procurementMethod == null ? "" : procurementMethod.getProcuremntMethod();
    }

    @Transient
    public String getProcurementTypeName() {
        return procurementType == null ? "" : procurementType.getProcuremntType();
    }

    @Transient
    public String getPrequalificationText() {
        return Boolean.TRUE.equals(prequalification) ? "Yes" : "No";
    }

    @Transient
    public String getReserveSchemeText() {
        return Boolean.TRUE.equals(reserveScheme) ? "Yes" : "No";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof BudgetItems other)) {
            return false;
        }

        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

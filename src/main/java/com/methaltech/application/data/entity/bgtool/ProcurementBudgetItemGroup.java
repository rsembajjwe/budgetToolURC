package com.methaltech.application.data.entity.bgtool;

import com.methaltech.application.data.ProcClass;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proc_budget_item_group")
@Getter
@Setter
@NoArgsConstructor
public class ProcurementBudgetItemGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coa_id")
    private COA coa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_unit_id")
    private UrcDeptSectionAnlDimbgt deptUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "proc_class")
    private ProcClass procClass;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "proc_budget_item_group_items",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "budget_item_id")
    )
    private Set<BudgetItems> items = new HashSet<>();

    @Column(name = "created_on")
    private LocalDateTime createdOn = LocalDateTime.now();

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

    @Column(name = "reservation_scheme_details")
    private String reservationSchemeDetails;

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

    public BigDecimal getTotalCost() {
        return items == null
                ? BigDecimal.ZERO
                : items.stream()
                        .map(BudgetItems::getYearTotalFromQuarters)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

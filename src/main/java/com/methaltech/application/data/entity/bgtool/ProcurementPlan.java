package com.methaltech.application.data.entity.bgtool;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.ProcurementMethodList;
import com.methaltech.application.data.ProcurementTypeList;
import jakarta.persistence.CascadeType;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "procurementPlan")
@NoArgsConstructor
public @Data
class ProcurementPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String subject;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "procPlanBudgetItems")
    private Set<BudgetItems> procPlanBudgetItems;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    @JsonIgnore
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "procurementMethod")
    @JsonIgnore
    private ProcurementMethod procurementMethod;

    @ManyToOne
    @JoinColumn(name = "coa_id")
    @JsonIgnore
    private COA coa;

    @Enumerated(EnumType.STRING)
    @Column(name = "procureClass_id")
    private ProcClass procClass;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @Column(name = "cost", precision = 25, scale = 6)
    private BigDecimal cost;
    
    @Column(name = "totalPerProc", precision = 25, scale = 6)
    private BigDecimal total;    

    @ManyToOne
    @JoinColumn(name = "fundsource_id")
    private Fundsource fundsource;

    @ManyToOne
    @JoinColumn(name = "contracttype_id")
    private ProcurementType procurementtype;

    @Column(name = "prequal")
    private Boolean prequal;

    @Column(name = "reserve")
    private Boolean reserve;

    @Column(name = "binvite")
    private LocalDate binvite;
    @Column(name = "reqInviofExpofInterestdate")
    private LocalDate reqInviofExpofInterestdate;

    @Column(name = "reqClosingOpeningdate")
    private LocalDate reqClosingOpeningdate;

    @Column(name = "Approvaloffinalevaluationreport")
    private LocalDate Approvaloffinalevaluationreport;

    @Column(name = "reqApprovalOfShortlist")
    private LocalDate reqApprovalOfShortlist;

    @Column(name = "Awardnotificationdate")
    private LocalDate Awardnotificationdate;
    @Column(name = "reqNotificationdate")
    private LocalDate reqNotificationdate;

    @Column(name = "InvitationofProposalsdate")
    private LocalDate InvitationofProposalsdate;

    @Column(name = "SubmissionOpeningdate")
    private LocalDate SubmissionOpeningdate;

    @Column(name = "InvNotificationdate")
    private LocalDate InvNotificationdate;

    @Column(name = "Contractsigningdate")
    private LocalDate Contractsigningdate;

    @Column(name = "bcompletion")
    private LocalDate bcompletion;

}

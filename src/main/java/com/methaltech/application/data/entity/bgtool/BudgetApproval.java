package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "BudgetApprovals")
public @Data
class BudgetApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Boolean bloSubmission;
    private LocalDateTime bloSubmissionDate;
    private Boolean hodSubmission;
    private LocalDateTime hodSubmissionDate;
    private Boolean maSubmission;
    private LocalDateTime maSubmissionDate;
    private Boolean cfoSubmission;
    private LocalDateTime cfoSubmissionDate;
    private Boolean maApproval;
    private LocalDateTime maApprovalDate;
    /*        @ManyToOne
    @JoinColumn(name = "dept_id")
    UrcDepartmentAnlDim dept;*/
    @ManyToOne
    @JoinColumn(name = "section_id", unique = true)
    private UrcDeptSectionAnlDimbgt section;
    @ManyToOne
    @JoinColumn(name = "budget_id")
    Budget budget;

}

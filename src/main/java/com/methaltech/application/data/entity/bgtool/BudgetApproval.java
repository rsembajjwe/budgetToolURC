package com.methaltech.application.data.entity.bgtool;

import com.methaltech.application.data.entity.livedata.UrcDepartmentAnlDim;
import com.methaltech.application.data.entity.livedata.UrcDeptSectionAnlDim;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
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

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "budget_messages")
    Set<BudgetApprovalMessages> messages;
}

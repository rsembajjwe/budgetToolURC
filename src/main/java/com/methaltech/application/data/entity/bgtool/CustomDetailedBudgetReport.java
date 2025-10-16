package com.methaltech.application.data.entity.bgtool;

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
import java.io.Serializable;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customBudgetReport")
@NoArgsConstructor
public @Data
class CustomDetailedBudgetReport  implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sheetname;
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "bdgt_detailReport")
    private Set<UrcDeptSectionAnlDimbgt> deptsection;
    @ManyToOne
    @JoinColumn(name = "customDetailedBudgetReportImp_id")
    private CustomDetailedBudgetReportImp budgetreport;
}

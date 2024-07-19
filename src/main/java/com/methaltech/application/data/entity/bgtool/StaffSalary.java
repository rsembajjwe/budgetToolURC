package com.methaltech.application.data.entity.bgtool;

import com.methaltech.application.data.salaryScale;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "staffSalary")
@NoArgsConstructor
public @Data
class StaffSalary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String fname;
    String lname;
    String tel;
    String mob;
    String Address;
    String Address2;
    String nextofkin;
    String email;
    BigDecimal salary;
    String position;
    String code;
    String contract;
    salaryScale grade;
    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Urc_Activities activity;
    @ManyToOne
    @JoinColumn(name = "Organisation_id")
    private Organisation budgetType;
    @ManyToOne
    @JoinColumn(name = "dsection_id")
    private UrcDeptSectionAnlDimbgt deptUnit;    
}

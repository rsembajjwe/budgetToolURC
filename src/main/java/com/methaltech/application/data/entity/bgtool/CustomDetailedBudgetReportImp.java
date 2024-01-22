package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customBudgetReportImp")
@NoArgsConstructor
public @Data class CustomDetailedBudgetReportImp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reportname;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

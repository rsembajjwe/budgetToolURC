package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SectionDeptUnitMerger")
@NoArgsConstructor
public @Data
class SectionDeptUnitMerger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sectioncode;

    private Set<String> deptUnitcodes;
    
}

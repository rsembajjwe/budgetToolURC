package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DeptSectionMerger")
@NoArgsConstructor
public @Data
class DeptSectionMerger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deptcode;
    //private Set<String> sectioncodes;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DeptSectionCodes", joinColumns = @JoinColumn(name = "dept_merger_id"))
    @Column(name = "section_code")
    private Set<String> sectioncodes;
}

package com.methaltech.application.data.entity.bgtool;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.Role;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "coa")
@NoArgsConstructor
@ToString(exclude = {"id", "code", "budget", "coalevel1", "coalevel11", "coalevel12", "coalevel13", "dsections"})
public @Data
class COA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   @Column(unique = true)
    private String code;
    private String name;
    private boolean stateOpen;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "coalevel1_id")
    private Coalevel1 coalevel1;

    @ManyToOne
    @JoinColumn(name = "coalevel11_id")
    private Coalevel11 coalevel11;

    @ManyToOne
    @JoinColumn(name = "coalevel12_id")
    private Coalevel12 coalevel12;

    @ManyToOne
    @JoinColumn(name = "coalevel13_id")
    private Coalevel13 coalevel13;

    @Enumerated(EnumType.STRING)
    @Column(name = "display")
    private Display display;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "coa_dsection")
    private Set<Section> dsections = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "PROC_CLASS") // You can customize the column name if needed
    private ProcClass procclass;
    
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "coa_deptsection")
    private Set<UrcDeptSectionAnlDimbgt> deptsection;
    String statCode;
   
}

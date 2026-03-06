package com.methaltech.application.data.entity.bgtool;

import com.methaltech.application.data.FundType;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Organisation")
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true) // Optional: to avoid lazy loading in toString
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Important!
public class Organisation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @EqualsAndHashCode.Include
    private String name;

    @EqualsAndHashCode.Include
    private String code;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @Enumerated(EnumType.STRING)
    @Column(name = "FundType")
    private FundType fundType;

    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<COA> coaAccounts = new HashSet<>();

    // Helper methods
    public void addCOA(COA coa) {
        coaAccounts.add(coa);
        coa.setOrganisation(this);
    }

    public void removeCOA(COA coa) {
        coaAccounts.remove(coa);
        coa.setOrganisation(null);
    }

    public int getCoaCount() {
        return coaAccounts != null ? coaAccounts.size() : 0;
    }

    public boolean hasCoaAccounts() {
        return coaAccounts != null && !coaAccounts.isEmpty();
    }
}

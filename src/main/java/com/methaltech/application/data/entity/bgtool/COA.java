package com.methaltech.application.data.entity.bgtool;

import com.methaltech.application.data.Classification1;
import com.methaltech.application.data.Classification2;
import com.methaltech.application.data.Classification3;
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
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
        name = "coa",
        uniqueConstraints = {
            @UniqueConstraint(name = "ux_coa_code_budget", columnNames = {"code", "budget_id"})
        }
)
@NoArgsConstructor
@ToString(exclude = {"id", "code", "budget", "coalevel1", "coalevel11", "coalevel12", "coalevel13", "dsections"})
public @Data
class COA implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@Column(unique = true)
    @Column(nullable = false, length = 50)
    private String code;
    private String name;
    private boolean stateOpen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "budget_id", nullable = false)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "CLASS1")
    private Classification1 class1;
    @Enumerated(EnumType.STRING)
    @Column(name = "CLASS2")
    private Classification2 class2;
    @Enumerated(EnumType.STRING)
    @Column(name = "CLASS3")
    private Classification3 class3;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "coa_deptsection")
    private Set<UrcDeptSectionAnlDimbgt> deptsection;

    String statCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    // Helper methods for organisation relationship
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Organisation getOrganisation() {
        return this.organisation;
    }

    public boolean isAssigned() {
        return this.organisation != null;
    }

    public String getOrganisationName() {
        return this.organisation != null ? this.organisation.getName() : "Unassigned";
    }

    public String getOrganisationCode() {
        return this.organisation != null ? this.organisation.getCode() : null;
    }

    // Helper methods for hierarchy
    public String getFullHierarchy() {
        StringBuilder hierarchy = new StringBuilder();

        if (coalevel1 != null) {
            hierarchy.append(coalevel1.getName());
        }

        if (coalevel11 != null) {
            if (hierarchy.length() > 0) {
                hierarchy.append(" > ");
            }
            hierarchy.append(coalevel11.getName());
        }

        if (coalevel12 != null) {
            if (hierarchy.length() > 0) {
                hierarchy.append(" > ");
            }
            hierarchy.append(coalevel12.getName());
        }

        if (coalevel13 != null) {
            if (hierarchy.length() > 0) {
                hierarchy.append(" > ");
            }
            hierarchy.append(coalevel13.getName());
        }

        return hierarchy.toString();
    }

    public String getShortHierarchy() {
        StringBuilder hierarchy = new StringBuilder();

        if (coalevel1 != null) {
            hierarchy.append(coalevel1.getCode() != null ? coalevel1.getCode() : coalevel1.getName());
        }

        if (coalevel11 != null) {
            if (hierarchy.length() > 0) {
                hierarchy.append("-");
            }
            // hierarchy.append(coalevel11.getCode() != null ? coalevel11.getCode() : coalevel11.getName());
        }

        return hierarchy.toString();
    }

    // Helper methods for sections
    public void addDsection(Section section) {
        if (dsections == null) {
            dsections = new HashSet<>();
        }
        dsections.add(section);
    }

    public void removeDsection(Section section) {
        if (dsections != null) {
            dsections.remove(section);
        }
    }

    public void addDeptsection(UrcDeptSectionAnlDimbgt deptsection) {
        if (this.deptsection == null) {
            this.deptsection = new HashSet<>();
        }
        this.deptsection.add(deptsection);
    }

    public void removeDeptsection(UrcDeptSectionAnlDimbgt deptsection) {
        if (this.deptsection != null) {
            this.deptsection.remove(deptsection);
        }
    }

    // Status and validation methods
    public boolean isActive() {
        return stateOpen;
    }

    public String getStatusText() {
        return stateOpen ? "Active" : "Inactive";
    }

    public String getStatusColor() {
        return stateOpen ? "#10b981" : "#6b7280";
    }

    public boolean hasHierarchy() {
        return coalevel1 != null || coalevel11 != null || coalevel12 != null || coalevel13 != null;
    }

    public int getHierarchyLevel() {
        if (coalevel13 != null) {
            return 4;
        }
        if (coalevel12 != null) {
            return 3;
        }
        if (coalevel11 != null) {
            return 2;
        }
        if (coalevel1 != null) {
            return 1;
        }
        return 0;
    }

    public boolean hasSections() {
        return (dsections != null && !dsections.isEmpty())
                || (deptsection != null && !deptsection.isEmpty());
    }

    public int getTotalSectionsCount() {
        int count = 0;
        if (dsections != null) {
            count += dsections.size();
        }
        if (deptsection != null) {
            count += deptsection.size();
        }
        return count;
    }

    // Validation methods
    public boolean isValidForAssignment() {
        return code != null && !code.trim().isEmpty()
                && name != null && !name.trim().isEmpty()
                && budget != null && stateOpen;
    }

    public String getValidationMessage() {
        if (code == null || code.trim().isEmpty()) {
            return "COA code is required";
        }
        if (name == null || name.trim().isEmpty()) {
            return "COA name is required";
        }
        if (budget == null) {
            return "Budget assignment is required";
        }
        if (!stateOpen) {
            return "COA must be active for assignment";
        }
        return null;
    }

    // Equals and hashCode based on code only to avoid lazy loading issues
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof COA other)) {
            return false;
        }

        if (code == null || other.code == null) {
            return false;
        }

        Long thisBudgetId = (budget != null ? budget.getId() : null);
        Long otherBudgetId = (other.budget != null ? other.budget.getId() : null);

        return code.equals(other.code) && java.util.Objects.equals(thisBudgetId, otherBudgetId);
    }

    @Override
    public int hashCode() {
        Long budgetId = (budget != null ? budget.getId() : null);
        return java.util.Objects.hash(code, budgetId);
    }
}

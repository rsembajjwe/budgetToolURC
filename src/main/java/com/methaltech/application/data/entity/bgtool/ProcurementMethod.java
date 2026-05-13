package com.methaltech.application.data.entity.bgtool;

import com.methaltech.application.data.ProcurementMethodList;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "procuremntMethod")
@NoArgsConstructor
@Getter
@Setter
public class ProcurementMethod implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String procuremntMethod;
    @Column(name = "procurementmethods_id")
    private ProcurementMethodList procurementmethodenum;
    @Column(unique = true) // Ensure uniqueness of num
    private Integer num;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof BudgetItems other)) {
            return false;
        }

        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.CascadeType;
import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "budget")
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // 👈 Restrict equals/hashCode to id
@ToString(onlyExplicitlyIncluded = true)          // 👈 Prevent recursion in toString
public class Budget implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include   // 👈 use only id in equals/hashCode
    @ToString.Include             // 👈 include id in toString
    private Long id;

    @ToString.Include
    private String financialYear;

    private LocalDate startDate;
    private LocalDate closeDate;
    private String description;
    private boolean active;
}

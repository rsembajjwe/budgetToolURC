
package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "freight_actual_volumes",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"budget_id", "route_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreightActualVolumes implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Monthly actual volumes
    @Column(precision = 18, scale = 4)
    private BigDecimal jul;

    @Column(precision = 18, scale = 4)
    private BigDecimal aug;

    @Column(precision = 18, scale = 4)
    private BigDecimal sep;

    @Column(precision = 18, scale = 4)
    private BigDecimal oct;

    @Column(precision = 18, scale = 4)
    private BigDecimal nov;

    @Column(precision = 18, scale = 4)
    private BigDecimal dec;

    @Column(precision = 18, scale = 4)
    private BigDecimal jan;

    @Column(precision = 18, scale = 4)
    private BigDecimal feb;

    @Column(precision = 18, scale = 4)
    private BigDecimal mar;

    @Column(precision = 18, scale = 4)
    private BigDecimal apr;

    @Column(precision = 18, scale = 4)
    private BigDecimal may;

    @Column(precision = 18, scale = 4)
    private BigDecimal jun;

    @Column(precision = 18, scale = 4)
    private BigDecimal total;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private COA coacode;

    @PrePersist
    @PreUpdate
    private void calculateTotal() {
        this.total = Stream.of(
                jul, aug, sep, oct, nov, dec,
                jan, feb, mar, apr, may, jun
        ).filter(Objects::nonNull)
         .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}


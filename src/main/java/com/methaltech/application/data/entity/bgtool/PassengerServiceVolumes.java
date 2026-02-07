
package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Stream;

@Entity
@Table(
        name = "passenger_service_volumes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"budget_id", "coa_id"})
)
@NoArgsConstructor
@Data
public class PassengerServiceVolumes implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coa_id", nullable = false)
    private COA coacode;

    @Column(precision = 18, scale = 4) private BigDecimal jul;
    @Column(precision = 18, scale = 4) private BigDecimal aug;
    @Column(precision = 18, scale = 4) private BigDecimal sep;
    @Column(precision = 18, scale = 4) private BigDecimal oct;
    @Column(precision = 18, scale = 4) private BigDecimal nov;
    @Column(precision = 18, scale = 4) private BigDecimal dec;

    @Column(precision = 18, scale = 4) private BigDecimal jan;
    @Column(precision = 18, scale = 4) private BigDecimal feb;
    @Column(precision = 18, scale = 4) private BigDecimal mar;
    @Column(precision = 18, scale = 4) private BigDecimal apr;
    @Column(precision = 18, scale = 4) private BigDecimal may;
    @Column(precision = 18, scale = 4) private BigDecimal jun;

    @Column(precision = 18, scale = 4) private BigDecimal total;

    @PrePersist
    @PreUpdate
    private void calcTotal() {
        this.total = Stream.of(jul, aug, sep, oct, nov, dec, jan, feb, mar, apr, may, jun)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}


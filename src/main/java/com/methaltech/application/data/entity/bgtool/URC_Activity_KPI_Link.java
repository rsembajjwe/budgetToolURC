
package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(
    name = "urc_activity_kpi_links",
    uniqueConstraints = @UniqueConstraint(columnNames = {"activity_id", "kpi_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class URC_Activity_KPI_Link implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Urc_Activities activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_id", nullable = false)
    private URC_Programme_KPI kpi;

    @Column(name = "link_notes", length = 1000)
    private String linkNotes;
}

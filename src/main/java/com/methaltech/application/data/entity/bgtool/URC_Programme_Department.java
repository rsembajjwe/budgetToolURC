
package com.methaltech.application.data.entity.bgtool;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(
        name = "urc_programme_departments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"programme_id", "department_code"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class URC_Programme_Department implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_id", nullable = false)
    private URC_Priority_Areas programme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_code", referencedColumnName = "department_code", nullable = false)
    private Custom_URC_Department department;

    @Column(name = "is_lead_department", nullable = false)
    private Boolean leadDepartment = false;

    @Column(name = "responsibility", length = 1000)
    private String responsibility;
}

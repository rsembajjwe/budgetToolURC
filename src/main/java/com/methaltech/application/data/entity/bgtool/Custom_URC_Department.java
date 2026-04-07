
package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "custom_urc_departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Custom_URC_Department implements Serializable {

    @Id
    @Column(name = "department_code", length = 20)
    @EqualsAndHashCode.Include
    private String departmentCode;

    @ToString.Include
    @Column(name = "department_name", nullable = false, length = 255)
    private String departmentName;

    @Column(name = "category_id", length = 20)
    private String categoryId;

    @Column(name = "source_anl_code", length = 20, unique = true)
    private String sourceAnlCode;

    @Column(name = "department_type", length = 50)
    private String departmentType;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "budget_check_enabled", nullable = false)
    private Boolean budgetCheckEnabled = false;

    @Column(name = "budget_stop_enabled", nullable = false)
    private Boolean budgetStopEnabled = false;

    @Column(name = "posting_prohibited", nullable = false)
    private Boolean postingProhibited = false;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "is_default_selection", nullable = false)
    private Boolean defaultSelection = false;
}

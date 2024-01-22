
package com.methaltech.application.data.entity.oldbgtool;

import jakarta.persistence.*;
import java.util.Date;
import java.math.BigDecimal;
import lombok.Data;

@Entity
@Table(name = "STAFF")
public @Data class OldStaffPojo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @Column(name = "FNAME", length = 50)
    private String firstName;

    @Column(name = "LNAME", length = 50)
    private String lastName;

    @Column(name = "TEL", length = 50)
    private String telephone;

    @Column(name = "MOB", length = 50)
    private String mobile;

    @Column(name = "ADDRESS", length = 50)
    private String address;

    @Column(name = "ADDRESS2", length = 50)
    private String address2;

    @Column(name = "NEXTOFKIN", length = 50)
    private String nextOfKin;

    @Column(name = "EMAIL", columnDefinition = "nvarchar(max)")
    private String email;

    @Column(name = "APPOINTMENT")
    @Temporal(TemporalType.DATE)
    private Date appointment;

    @Column(name = "DEPARTURE")
    @Temporal(TemporalType.DATE)
    private Date departure;

    @Column(name = "POSITION", length = 50)
    private String position;

    @Column(name = "CODE", length = 50)
    private String code;

    @Column(name = "SALARY")
    private BigDecimal salary;

    @Column(name = "CONTRACT", length = 50)
    private String contract;

    @Column(name = "GRADE", length = 50)
    private String grade;

    @Column(name = "FY", length = 50)
    private String fiscalYear;
}

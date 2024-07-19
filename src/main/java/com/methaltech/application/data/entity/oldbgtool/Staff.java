
package com.methaltech.application.data.entity.oldbgtool;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Entity
@Table(name = "STAFF")
public @Data class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "FNAME")
    private String fname;

    @Column(name = "LNAME")
    private String lname;

    @Column(name = "TEL")
    private String tel;

    @Column(name = "MOB")
    private String mob;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "ADDRESS2")
    private String address2;

    @Column(name = "NEXTOFKIN")
    private String nextOfKin;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "APPOINTMENT")
    @Temporal(TemporalType.DATE)
    private Date appointment;

    @Column(name = "DEPARTURE")
    @Temporal(TemporalType.DATE)
    private Date departure;

    @Column(name = "POSITION")
    private String position;

    @Column(name = "CODE")
    private String code;

    @Column(name = "SALARY")
    private BigDecimal salary;

    @Column(name = "CONTRACT")
    private String contract;

    @Column(name = "GRADE")
    private String grade;

    @Column(name = "FY")
    private String fy;
}


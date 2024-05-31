package com.methaltech.application.data.entity.bgtool;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.methaltech.application.data.Role;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import java.util.List;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "URC_USER")
@NoArgsConstructor
@Data

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "ROLES")
    private Set<Role> roles;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL_VERIFICATION_HASH")
    private String emailVerificationHash;

    @Column(name = "EMAIL_VERIFICATION_ATTEMPTS")
    private int emailVerificationAttempts;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_TIME")
    private String createdTime;

    @JsonIgnore
    @Column(name = "PASSWORD")
    private String hashedPassword;

    @Column(name = "TEL")
    private String tel;

    @Column(name = "UNIT")
    private Integer unit;

    @Column(name = "SECTION")
    private Integer section;

    @Column(name = "DEPT")
    private Integer dept;

    @Column(name = "VERIFYEMAIL")
    private String verifyEmail;

    @Lob
    @Column(length = 1000000)
    private byte[] profilePicture;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<UnitsBudget> unitsbudget;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_section",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "ANL_CODE_id")
    )
    private Set<UrcDeptSectionAnlDimbgt> deptsection;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_unit",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "unit_id")
    )
    private Set<D_Unit> units;

    /*    @ManyToOne
    @JoinColumn(name = "department_id")*/
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_department",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "userdepartment_id"))
    private Set<UrDepartmentsAnlDim2> department;
}

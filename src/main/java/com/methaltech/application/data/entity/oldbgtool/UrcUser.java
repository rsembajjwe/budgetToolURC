
package com.methaltech.application.data.entity.oldbgtool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "URC_USER")
public @Data class UrcUser {

    @Id
    @Column(name = "USER_ID")
    private Integer userId;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "EMAIL_VERIFICATION_HASH")
    private String emailVerificationHash;

    @Column(name = "EMAIL_VERIFICATION_ATTEMPTS")
    private Integer emailVerificationAttempts;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_TIME")
    private String createdTime;

    @Column(name = "PASSWORD")
    private String password;

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
}

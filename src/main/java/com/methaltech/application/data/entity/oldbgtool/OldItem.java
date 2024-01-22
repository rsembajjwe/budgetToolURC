package com.methaltech.application.data.entity.oldbgtool;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ITEM")
public @Data class OldItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "STCODE")
    private String stCode;

    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CATEGORY")
    private String category;

    @Column(name = "CLASS")
    private String itemClass;

    @Column(name = "CAT")
    private String cat;
}

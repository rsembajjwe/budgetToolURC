package com.methaltech.application.data.entity.oldbgtool;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "CoaMatching")
public @Data
class CoaMatching {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String oldCoa;
    String newCoa;
}

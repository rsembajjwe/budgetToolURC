package com.methaltech.application.data.entity.bgtool;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fundsource")
@NoArgsConstructor
public @Data class Fundsource  implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String fundsource;
    private String code;
    @ManyToOne
    @JoinColumn(name = "budget_id")
    @JsonIgnore
    private Budget budget; 
   
}

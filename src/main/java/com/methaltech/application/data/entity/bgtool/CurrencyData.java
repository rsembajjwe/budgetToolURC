
package com.methaltech.application.data.entity.bgtool;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "currencyData")
@NoArgsConstructor
public @Data
class CurrencyData implements Serializable {

    private String currency;
    private String currencyShort;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public String toString() {
        return currency;
    }

}

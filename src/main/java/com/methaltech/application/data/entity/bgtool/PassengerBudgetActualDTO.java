
package com.methaltech.application.data.entity.bgtool;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PassengerBudgetActualDTO {

    @EqualsAndHashCode.Include
    private Long coaId;

    private COA coacode;
    private Budget budget;
    private PassengerServiceVolumes budgetRow;
    private PassengerActualVolumes actualRow;

    public void setCoacode(COA coacode) {
        this.coacode = coacode;
        this.coaId = coacode != null ? coacode.getId() : null;
    }
}




package com.methaltech.application.data.entity.bgtool;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PassengerRowDTO {
    private Budget budget;
    private COA coacode;

    private PassengerServiceVolumes budgetRow; // editable
    private PassengerActualVolumes actualRow;  // editable
}


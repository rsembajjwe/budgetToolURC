
package com.methaltech.application.data;

import com.methaltech.application.data.entity.bgtool.D_Unit;
import java.util.List;
import lombok.Data;


public class ListUnitCOA {
private List<D_Unit> dunit;

    public List<D_Unit> getDunit() {
        return dunit;
    }

    public void setDunit(List<D_Unit> dunit) {
        this.dunit = dunit;
    }


}


package com.methaltech.application.data.entity.bgtool;

import java.io.Serializable;


public class RowsWorkplan  implements Serializable{
 Integer rowStart;
 Integer rowEnd;

    public RowsWorkplan(Integer rowStart, Integer rowEnd) {
        this.rowStart = rowStart;
        this.rowEnd = rowEnd;
    }

    public Integer getRowStart() {
        return rowStart;
    }

    public void setRowStart(Integer rowStart) {
        this.rowStart = rowStart;
    }

    public Integer getRowEnd() {
        return rowEnd;
    }

    public void setRowEnd(Integer rowEnd) {
        this.rowEnd = rowEnd;
    }

    @Override
    public String toString() {
        return "RowsWorkplan{" + "rowStart=" + rowStart + ", rowEnd=" + rowEnd + '}';
    }
 
}

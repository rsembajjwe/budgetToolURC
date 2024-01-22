
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.COA;

public class GridFilter {
    private String searchTerm;

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getSearchTerm() {
        return searchTerm;
    }
    

    public boolean testCoa(COA coa) {
        boolean matchesFullName = matchesCoa(coa.getName(), searchTerm);
        boolean matchesProfession = matchesCoa(coa.getCode(), searchTerm);
        return matchesFullName || matchesProfession;
    }

    private boolean matchesCoa(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty()
                || value.toLowerCase().contains(searchTerm.toLowerCase());
    }    
}

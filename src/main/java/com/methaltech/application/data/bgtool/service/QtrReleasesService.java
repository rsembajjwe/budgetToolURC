
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.QtrReleases;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import java.util.List;
import java.util.Optional;

public interface QtrReleasesService {

    QtrReleases save(QtrReleases qtrReleases);

    QtrReleases update(QtrReleases qtrReleases);

    Optional<QtrReleases> findById(Long id);

    List<QtrReleases> findAll();

    void deleteById(Long id);

    Optional<QtrReleases> findByOrganisationBudgetSection(
            Organisation organisation,
            Budget budget,
            UrcDeptSectionAnlDimbgt deptSection
    );

    QtrReleases getOrCreate(
            Organisation organisation,
            Budget budget,
            UrcDeptSectionAnlDimbgt deptSection
    );

    List<QtrReleases> findByOrganisationAndBudget(Organisation organisation, Budget budget);

    List<QtrReleases> findByBudget(Budget budget);
}


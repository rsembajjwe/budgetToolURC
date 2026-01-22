package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.QtrReleases;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QtrReleasesRepository extends JpaRepository<QtrReleases, Long> {

    Optional<QtrReleases> findByOrganisationAndBudgetAndDeptSection(
            Organisation organisation,
            Budget budget,
            UrcDeptSectionAnlDimbgt deptSection
    );

    List<QtrReleases> findByOrganisation(Organisation organisation);

    List<QtrReleases> findByBudget(Budget budget);

    List<QtrReleases> findByOrganisationAndBudget(Organisation organisation, Budget budget);

    List<QtrReleases> findByBudgetAndDeptSection(Budget budget, UrcDeptSectionAnlDimbgt deptSection);

    boolean existsByOrganisationAndBudgetAndDeptSection(
            Organisation organisation,
            Budget budget,
            UrcDeptSectionAnlDimbgt deptSection
    );
}

package com.methaltech.application.data.oldbgtool.repository;

import com.methaltech.application.data.entity.oldbgtool.ProgramActivity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramActivityRepository extends JpaRepository<ProgramActivity, Integer> {

    List<ProgramActivity> findByProg(Integer programme);
}

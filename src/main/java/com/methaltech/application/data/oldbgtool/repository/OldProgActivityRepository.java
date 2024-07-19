package com.methaltech.application.data.oldbgtool.repository;

import com.methaltech.application.data.entity.oldbgtool.OldProgActivity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OldProgActivityRepository extends JpaRepository<OldProgActivity, Integer> {

    List<OldProgActivity> findByProg(Integer prog);
}

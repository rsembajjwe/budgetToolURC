
package com.methaltech.application.data.oldbgtool.repository;

import com.methaltech.application.data.entity.oldbgtool.Programme;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgrammeRepository  extends JpaRepository<Programme, Integer>{
    List<Programme> findByFy(String financialYear);
    
}

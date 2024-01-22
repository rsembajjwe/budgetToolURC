package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Coalevel11;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface Coalevel11Repository extends JpaRepository<Coalevel11, Long> {

    List<Coalevel11> findByCoalevel1(Coalevel1 coalevel1);

    List<Coalevel11> findAllByCoalevel1(Coalevel1 coalevel1);

    @Query("DELETE FROM Coalevel11 s WHERE s.coalevel1 = :coalevel1")
    List<Coalevel11> deleteByCoalevel11Id(@Param("coalevel1") Coalevel1 coalevel1);

    Coalevel11 findTopByOrderByIdDesc();
    
     Coalevel11 findByCoalevel1AndName(Coalevel1 coalevel1, String name);
     Coalevel11 findByName(String name);
}

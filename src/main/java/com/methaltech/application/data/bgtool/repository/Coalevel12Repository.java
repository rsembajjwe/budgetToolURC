
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Coalevel11;
import com.methaltech.application.data.entity.bgtool.Coalevel12;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface Coalevel12Repository extends JpaRepository<Coalevel12, Long>{
    
    List<Coalevel12> findByCoalevel1(Coalevel1 coalevel1);
    
    Coalevel12 findByCoalevel1AndName(Coalevel1 coalevel1,String name);
    
    @Query("DELETE FROM Coalevel12 s WHERE s.coalevel1 = :coalevel1")
    List<Coalevel11> deleteByCoalevel12Id(@Param("coalevel1") Coalevel1 coalevel1); 
    
    
}

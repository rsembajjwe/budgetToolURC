
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.Coalevel11;
import com.methaltech.application.data.entity.bgtool.Coalevel13;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface Coalevel13Repository extends JpaRepository<Coalevel13, Long>{
List<Coalevel13> findByCoalevel11(Coalevel11 coalevel11);    
Coalevel13 findByCoalevel11AndName(Coalevel11 coalevel11,String name);
}

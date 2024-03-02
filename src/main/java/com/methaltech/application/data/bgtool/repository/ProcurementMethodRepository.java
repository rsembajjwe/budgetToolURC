
package com.methaltech.application.data.bgtool.repository;
import com.methaltech.application.data.entity.bgtool.ProcurementMethod;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcurementMethodRepository extends JpaRepository<ProcurementMethod, Long> {
    ProcurementMethod findByNum(Integer num);
    // Add custom query methods if needed
}


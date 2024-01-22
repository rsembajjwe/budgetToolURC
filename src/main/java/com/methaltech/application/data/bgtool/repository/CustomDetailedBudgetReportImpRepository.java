
package com.methaltech.application.data.bgtool.repository;
import com.methaltech.application.data.entity.bgtool.CustomDetailedBudgetReportImp;
import com.methaltech.application.data.entity.bgtool.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomDetailedBudgetReportImpRepository extends JpaRepository<CustomDetailedBudgetReportImp, Long> {
    List<CustomDetailedBudgetReportImp> findByUser(User user);
}


package com.methaltech.application.data.bgtool.repository;


import com.methaltech.application.data.entity.bgtool.CurrencyEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyEntityRepository extends JpaRepository<CurrencyEntity, Integer> {

    List<CurrencyEntity> findByFy(String fy);


}

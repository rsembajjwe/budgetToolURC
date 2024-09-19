package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.CurrencyData;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CurrencyDataRepository extends JpaRepository<CurrencyData, Long> {

    CurrencyData findByCurrency(String currency);

    @Modifying
    @Query("update CurrencyData u set u.currency = ?1, u.currencyShort = ?2 where u.id = ?3")
    int setUserInfoById(String a, String b, Long c);

    List<CurrencyData> findByCurrencyShort(String currencyShort);

    @Query("SELECT c FROM CurrencyData c ORDER BY c.id DESC")
    Page<CurrencyData> findLastSavedItem(Pageable pageable);
}

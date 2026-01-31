package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.QuarterlyActuals;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface QuarterlyActualsRepository extends JpaRepository<QuarterlyActuals, Long> {

    Optional<QuarterlyActuals> findByAccountCodeAndPeriodAndTransactionDateTimeAndJournalNoAndJournalLine(
            String accountCode,
            Integer period,
            LocalDateTime transactionDateTime,
            Integer journalNo,
            Integer journalLine
    );

    List<QuarterlyActuals> findByPeriodIn(Set<Integer> periods);

    @Query("""
        SELECT COALESCE(SUM(q.amount), 0)
        FROM QuarterlyActuals q
        WHERE q.period IN :periods
          AND q.activity = :activity
    """)
    BigDecimal sumAmountByPeriodsAndActivity(
            @Param("periods") Set<Integer> periods,
            @Param("activity") Urc_Activities activity
    );
}

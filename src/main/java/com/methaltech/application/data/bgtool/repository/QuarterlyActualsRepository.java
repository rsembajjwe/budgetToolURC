
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.QuarterlyActuals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
}

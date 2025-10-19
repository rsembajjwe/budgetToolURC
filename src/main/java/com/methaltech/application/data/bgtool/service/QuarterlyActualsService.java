
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.QuarterlyActualsRepository;
import com.methaltech.application.data.entity.bgtool.QuarterlyActuals;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class QuarterlyActualsService {

    private final QuarterlyActualsRepository quarterlyActualsRepository;

    public QuarterlyActualsService(QuarterlyActualsRepository quarterlyActualsRepository) {
        this.quarterlyActualsRepository = quarterlyActualsRepository;
    }

    public QuarterlyActuals save(QuarterlyActuals quarterlyActuals) {
        return quarterlyActualsRepository.save(quarterlyActuals);
    }

    public Optional<QuarterlyActuals> findActualByID(
            String accountCode,
            Integer period,
            LocalDateTime transactionDateTime,
            Integer journalNo,
            Integer journalLine
    ) {
        return quarterlyActualsRepository
                .findByAccountCodeAndPeriodAndTransactionDateTimeAndJournalNoAndJournalLine(
                        accountCode, period, transactionDateTime, journalNo, journalLine
                );
    }

    // other CRUD methods...
}

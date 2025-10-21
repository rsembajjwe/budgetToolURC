package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.QuarterlyActualsRepository;
import com.methaltech.application.data.entity.bgtool.QuarterlyActuals;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.entity.livedata.SALFLDG;
import com.methaltech.application.data.livedata.repository.SALFLDGProjection;
import com.methaltech.application.data.livedata.repository.SALFLDGRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuarterlyActualsService {

    private final QuarterlyActualsRepository quarterlyActualsRepository;
    private final SALFLDGRepository SALFLDGRepository;

    public QuarterlyActualsService(QuarterlyActualsRepository quarterlyActualsRepository, SALFLDGRepository SALFLDGRepository) {
        this.quarterlyActualsRepository = quarterlyActualsRepository;
        this.SALFLDGRepository = SALFLDGRepository;
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

    public List<QuarterlyActuals> getQuarterlyActuals(String analT1, Set<Integer> periods, Urc_Activities activity) {
        List<SALFLDGProjection> salfldgs = SALFLDGRepository.findByPeriodAndDepartmentExpenditures(periods, analT1);
        return salfldgs.stream().map(s -> {
            QuarterlyActuals qa = new QuarterlyActuals();
            qa.setAccountCode(s.getAccntCode());
            qa.setPeriod(s.getPeriod());
            qa.setTransactionDateTime(s.getTransDatetime() != null
                    ? s.getTransDatetime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null);
            qa.setJournalNo(s.getJrnalNo());
            qa.setJournalLine(s.getJrnalLine());
            //qa.setJournalLine(s.getJrnalLine());
            qa.setAmount(s.getAmount() != null ? s.getAmount() : BigDecimal.ZERO);
            qa.setDescription(s.getDescriptn()); // set parent activity
            return qa;
        }).collect(Collectors.toList());
    }
}

package com.methaltech.application.data.livedata.service;

import com.methaltech.application.data.entity.livedata.URC_ACNT;
import com.methaltech.application.data.entity.livedata.UrcAcnt;
import com.methaltech.application.data.livedata.repository.UrcAcntRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrcAcntService {

    private final UrcAcntRepository urcAcntRepository;

    @Autowired
    public UrcAcntService(UrcAcntRepository urcAcntRepository) {
        this.urcAcntRepository = urcAcntRepository;
    }

    public List<UrcAcnt> getAllUrcAcnts() {
        return urcAcntRepository.findAll();
    }

    public UrcAcnt getUrcAcntById(Long id) {
        return urcAcntRepository.findById(id).orElse(null);
    }

    public List<URC_ACNT> findByAcntCodeStartingWith(String startsWith) {
        return urcAcntRepository.findByAcntCodeStartingWith(startsWith);
    }
    
        public List<URC_ACNT> findByAcntCode(String startsWith) {
        return urcAcntRepository.findByAcntCode(startsWith);
    }

    public List<URC_ACNT> findByAcntCodeStartingWith11110AndNextDigitIn123(String startsWith, List<String> string) {

        return urcAcntRepository.findByAcntCodeStartingWith11110AndNextDigitIn(startsWith, string);

    }
}


package com.methaltech.application.data.livedata.service;
import com.methaltech.application.data.entity.livedata.UrcAnlCode;
import com.methaltech.application.data.livedata.repository.UrcAnlCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UrcAnlCodeService {
    private final UrcAnlCodeRepository urcAnlCodeRepository;

    @Autowired
    public UrcAnlCodeService(UrcAnlCodeRepository urcAnlCodeRepository) {
        this.urcAnlCodeRepository = urcAnlCodeRepository;
    }

    public UrcAnlCode saveUrcAnlCode(UrcAnlCode urcAnlCode) {
        return urcAnlCodeRepository.save(urcAnlCode);
    }

    public List<UrcAnlCode> getAllUrcAnlCodes() {
        return urcAnlCodeRepository.findAll();
    }
    public List<UrcAnlCode> findByAnlCatIdAndAnlCodeStartingWith(String anlCatId,String prefix) {
        return urcAnlCodeRepository.findByAnlCatIdAndAnlCodeStartingWith(anlCatId,prefix);
    }
}

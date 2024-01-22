
package com.methaltech.application.data.livedata.service;

import com.methaltech.application.data.entity.livedata.UrcAcnt;
import com.methaltech.application.data.livedata.repository.UrcAcntRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UrcAcntService{

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

}


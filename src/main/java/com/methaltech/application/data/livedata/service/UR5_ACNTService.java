package com.methaltech.application.data.livedata.service;

import com.methaltech.application.data.entity.livedata.UR5_ACNT;
import com.methaltech.application.data.livedata.repository.UR5_ACNTRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UR5_ACNTService {

    private final UR5_ACNTRepository UR5_ACNTRepository;

    @Autowired
    public UR5_ACNTService(UR5_ACNTRepository UR5_ACNTRepository) {
        this.UR5_ACNTRepository = UR5_ACNTRepository;
    }

    public UR5_ACNT findById(String id) {
        return UR5_ACNTRepository.findById(id).orElse(null);
    }

    public UR5_ACNT save(UR5_ACNT urc5_acnt) {
        return UR5_ACNTRepository.save(urc5_acnt);
    }

    public void deleteById(String id) {
        UR5_ACNTRepository.deleteById(id);
    }

    public Page<UR5_ACNT> list(Pageable pageable) {
        return UR5_ACNTRepository.findAll(pageable);
    }

    public List<UR5_ACNT> findAll() {
        return UR5_ACNTRepository.findAll();
    }

    public List<UR5_ACNT> getUr5AcntByCriteria() {
       // return UR5_ACNTRepository.findByAcntCodeStartingWithAndAcntSubTypeGreaterThanAndAcntSubTypeLessThan("1", 5, 9);
        return null;
    }

    public List<UR5_ACNT> findByAcntCodeStartingWithAndAcntCodeGreaterThanAndAcntCodeLessThan() {
        return UR5_ACNTRepository.findByAcntCodeStartingWithAndAcntCodeGreaterThanAndAcntCodeLessThan();

    }
    public List<UR5_ACNT> findByAcntCodeStartingWithAndAcntCodeLengthRange(String search,String code) {
        return UR5_ACNTRepository.findByAcntCodeStartingWithAndAcntCodeLengthRange(search,code);

    }
   public  UR5_ACNT findByAcntCode(String AcntCode){
     return UR5_ACNTRepository.findByAcntCode(AcntCode);
   }
}

package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Coalevel12;
import com.methaltech.application.data.bgtool.repository.Coalevel12Repository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Coalevel12Service {

    private final Coalevel12Repository coalevel12Repository;

    @Autowired
    public Coalevel12Service(Coalevel12Repository coalevel12Repository) {
        this.coalevel12Repository = coalevel12Repository;
    }

    public List<Coalevel12> findAll() {
        return coalevel12Repository.findAll();
    }

    public Coalevel12 findById(Long id) {
        return coalevel12Repository.findById(id).orElse(null);
    }

    public Coalevel12 save(Coalevel12 coalevel12) {
        return coalevel12Repository.save(coalevel12);
    }

    public void deleteById(Long id) {
        coalevel12Repository.deleteById(id);
    }

    public List<Coalevel12> findCoalevel12ByClass1Id(Coalevel1 coa) {
        return coalevel12Repository.findByCoalevel1(coa);
    }
    public List<Coalevel12> findByCoalevel1(Coalevel1 coa) {
        return coalevel12Repository.findByCoalevel1(coa);
    }    

    public void deleteByCoalevel12Id(Long id) {
        coalevel12Repository.deleteById(id);
    }
}

package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Coalevel11;
import com.methaltech.application.data.bgtool.repository.Coalevel11Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Coalevel11Service {

    private final Coalevel11Repository coalevel11Repository;

    @Autowired
    public Coalevel11Service(Coalevel11Repository coalevel11Repository) {
        this.coalevel11Repository = coalevel11Repository;
    }

    public List<Coalevel11> findAll() {
        return coalevel11Repository.findAll();
    }

    public Coalevel11 findById(Long id) {
        return coalevel11Repository.findById(id).orElse(null);
    }
    public Coalevel11 findByName(String name) {
        return coalevel11Repository.findByName(name);
    }    

    public Coalevel11 save(Coalevel11 coalevel11) {
        return coalevel11Repository.save(coalevel11);
    }

    public void deleteById(Long id) {
        coalevel11Repository.deleteById(id);
    }

    public void deleteByCoalevel11Id(Long id) {
        coalevel11Repository.deleteById(id);
    }

    public List<Coalevel11> findCoalevel11ByClass1Id(Coalevel1 coa) {
        return coalevel11Repository.findByCoalevel1(coa);
    }

    public Optional<Coalevel11> findByIdOptional(Long id) {
        return coalevel11Repository.findById(id);
    }

    public List<Coalevel11> findByCoalevel11(Coalevel1 coa) {
        return coalevel11Repository.findByCoalevel1(coa);
    }

    public Coalevel11 getLastSavedCoalevel11() {
        return coalevel11Repository.findTopByOrderByIdDesc();
    }
}


package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.Coalevel11;
import com.methaltech.application.data.entity.bgtool.Coalevel13;
import com.methaltech.application.data.bgtool.repository.Coalevel13Repository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Coalevel13Service {
    private final Coalevel13Repository coalevel13Repository;

    @Autowired
    public Coalevel13Service(Coalevel13Repository coalevel13Repository) {
        this.coalevel13Repository = coalevel13Repository;
    }

    public List<Coalevel13> findAll() {
        return coalevel13Repository.findAll();
    }

    public Coalevel13 findById(Long id) {
        return coalevel13Repository.findById(id).orElse(null);
    }

    public Coalevel13 save(Coalevel13 coalevel13) {
        return coalevel13Repository.save(coalevel13);
    }

    public void deleteById(Long id) {
        coalevel13Repository.deleteById(id);
    }

    public List<Coalevel13> findCoalevel13ByClass1Id(Coalevel11 coa) {
        return coalevel13Repository.findByCoalevel11(coa);
    }

    public void deleteByCoalevel13Id(Long id) {
        coalevel13Repository.deleteById(id);
    }    
}

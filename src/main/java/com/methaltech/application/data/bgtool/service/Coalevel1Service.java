package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.bgtool.repository.Coalevel1Repository;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Coalevel1Service {

    private final Coalevel1Repository coalevel1Repository;

    @Autowired
    public Coalevel1Service(Coalevel1Repository coalevel1Repository) {
        this.coalevel1Repository = coalevel1Repository;
    }

    public List<Coalevel1> findAll() {
        return coalevel1Repository.findAll();
    }
    public List<Coalevel1> findByBudget() {
        return coalevel1Repository.findAll();
    }    

    public Coalevel1 findById(Long id) {
        return coalevel1Repository.findById(id).orElse(null);
    }

    public Coalevel1 save(Coalevel1 coalevel1) {
        return coalevel1Repository.save(coalevel1);
    }

    public void deleteById(Long id) {
        coalevel1Repository.deleteById(id);
    }

    public Page<Coalevel1> list(Pageable pageable) {
        return coalevel1Repository.findAll(pageable);
    }

    public List<Coalevel1> findCoalevel1ByBudgetId() {
        return coalevel1Repository.findAll();
    }

    public Coalevel1 findByNameAndBudget(String name) {
        return coalevel1Repository.findByNameAndBudget(name);
    }
    public Coalevel1 findByCode(Integer num) {
        return coalevel1Repository.findByCode(num);
    }

    public boolean existsByBudgetAndName(String name) {
        return coalevel1Repository.existsByName(name);
    }
    public void createCoalevel1ForNewBudget(){
       Coalevel1 coa1=new Coalevel1();
       coa1.setName("Income");
       //coa1.setBudget(budget);
       Coalevel1 coa2=new Coalevel1();
       coa2.setName("Operation Expenditure");
       //coa2.setBudget(budget);
       Coalevel1 coa3=new Coalevel1();
       coa3.setName("Capital Expenditure");
       //coa3.setBudget(budget);
       List<Coalevel1> list=Arrays.asList(coa1,coa2,coa2);
       
       coalevel1Repository.saveAll(list);
        
    }
}

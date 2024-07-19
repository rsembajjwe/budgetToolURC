package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.FundsourceRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FundsourceService {

    private final FundsourceRepository fundsourceRepository;

    @Autowired
    public FundsourceService(FundsourceRepository fundsourceRepository) {
        this.fundsourceRepository = fundsourceRepository;
    }

    // Save a new fundsource
    public Fundsource save(Fundsource fundsource) {
        return fundsourceRepository.save(fundsource);
    }

    // Get all fundsources
    public List<Fundsource> getAllFundsources() {
        return fundsourceRepository.findAll();
    }

    // Get a fundsource by ID
    public Optional<Fundsource> getFundsourceById(Long id) {
        return fundsourceRepository.findById(id);
    }

    // Delete a fundsource by ID
    public void deleteFundsourceById(Long id) {
        fundsourceRepository.deleteById(id);
    }

    // Delete a fundsource by object
    public void deleteFundsource(Fundsource fundsource) {
        fundsourceRepository.delete(fundsource);
    }

    // Find fundsources by budget
    public List<Fundsource> findFundsourcesByBudget(Budget budget) {
        return fundsourceRepository.findByBudget(budget);
    }

    public Fundsource findByFundsourceAndBudget(String fundsource, Budget budget) {
        return fundsourceRepository.findByFundsourceAndBudget(fundsource, budget);
    }

    public Fundsource findById(Long id) {
        Optional<Fundsource> optionalFundsource = fundsourceRepository.findById(id);
        return optionalFundsource.orElseThrow(() -> new IllegalArgumentException("Fundsource not found"));
    }

    public Fundsource getLastSavedFundsourceByBudget(Budget budget) {
        return fundsourceRepository.findTopByBudgetOrderByIdDesc(budget);
    }
}

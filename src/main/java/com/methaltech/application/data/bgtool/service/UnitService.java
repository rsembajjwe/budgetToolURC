package com.methaltech.application.data.bgtool.service;


import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.Section;
import com.methaltech.application.data.bgtool.repository.UnitRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UnitService {

    private final UnitRepository repository;

    @Autowired
    public UnitService(UnitRepository repository) {
        this.repository = repository;
    }

    public Optional<D_Unit> get(Long id) {
        return repository.findById(id);
    }

    public Optional<D_Unit> getUnitById(Long id) {
        return repository.findById(id);

    }

    @Transactional
    public D_Unit update(D_Unit entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<D_Unit> list(Pageable pageable) {
        return repository.findAll(pageable);
    }
    public List<D_Unit> list() {
        return repository.findAll();
    }    

    public List<D_Unit> list2(Long id) {
        return repository.findById(id).stream().toList();
    }

    public List<D_Unit> findBySection(Section section) {
        return repository.findBySection(section);
    }

    public List<D_Unit> findByDepartmentBudget(Budget budget) {
    return repository.findByDepartmentBudget(budget);
    }

    public int count() {
        return (int) repository.count();
    }
}

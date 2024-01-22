
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.National_Budget_Focus_Areas;
import com.methaltech.application.data.entity.bgtool.National_Transport_Master_Plan;
import com.methaltech.application.data.bgtool.repository.National_Budget_Focus_AreasRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class National_Budget_Focus_AreasService {

    private final National_Budget_Focus_AreasRepository repository;

    @Autowired
    public National_Budget_Focus_AreasService(National_Budget_Focus_AreasRepository repository) {
        this.repository = repository;
    }

    public Optional<National_Budget_Focus_Areas> get(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public National_Budget_Focus_Areas update(National_Budget_Focus_Areas entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<National_Budget_Focus_Areas> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<National_Budget_Focus_Areas> listByNationalTransportMasterPlan(National_Transport_Master_Plan nationalTransportMasterPlan,Pageable pageable) {
        return repository.findByNationalTransportMasterPlan(nationalTransportMasterPlan,pageable);
    }    
    
    public int count() {
        return (int) repository.count();
    }    
}

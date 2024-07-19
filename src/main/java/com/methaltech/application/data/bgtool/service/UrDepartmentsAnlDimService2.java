
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.entity.bgtool.UrDepartmentsAnlDim2;
import com.methaltech.application.data.entity.livedata.UrcDepartmentAnlDim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import com.methaltech.application.data.bgtool.repository.UrDepartmentsAnlDimRepository2;

@Service
public class UrDepartmentsAnlDimService2 {

    @Autowired
    private UrDepartmentsAnlDimRepository2 repository;

    public List<UrDepartmentsAnlDim2> findAll() {
        return repository.findAll();
    }

    public Optional<UrDepartmentsAnlDim2> findById(Long id) {
        return repository.findById(id);
    }

    public UrDepartmentsAnlDim2 save(UrDepartmentsAnlDim2 entity) {
        return repository.save(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

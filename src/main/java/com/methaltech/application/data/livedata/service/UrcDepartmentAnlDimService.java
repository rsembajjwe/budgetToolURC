
package com.methaltech.application.data.livedata.service;


import com.methaltech.application.data.entity.livedata.UrcDepartmentAnlDim;
import com.methaltech.application.data.livedata.repository.UrcDepartmentAnlDimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UrcDepartmentAnlDimService {

    private final UrcDepartmentAnlDimRepository repository;

    @Autowired
    public UrcDepartmentAnlDimService(UrcDepartmentAnlDimRepository repository) {
        this.repository = repository;
    }

    public List<UrcDepartmentAnlDim> getAllUrcDepartmentAnlDims() {
        return repository.findByANL_CODEStartingWithD();
    }    

    public Optional<UrcDepartmentAnlDim> getUrcDepartmentAnlDimById(String id) {
        return repository.findById(id);
    }

    public UrcDepartmentAnlDim createUrcDepartmentAnlDim(UrcDepartmentAnlDim urcDepartmentAnlDim) {
        return repository.save(urcDepartmentAnlDim);
    }

    public UrcDepartmentAnlDim updateUrcDepartmentAnlDim(String id, UrcDepartmentAnlDim updatedUrcDepartmentAnlDim) {
        if (repository.existsById(id)) {
            updatedUrcDepartmentAnlDim.setANL_CODE(id);
            return repository.save(updatedUrcDepartmentAnlDim);
        }
        return null; // Entity with the given ID not found
    }

    public void deleteUrcDepartmentAnlDim(String id) {
        repository.deleteById(id);
    }
}


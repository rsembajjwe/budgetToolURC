package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.CustomURCDepartmentRepository;
import com.methaltech.application.data.entity.bgtool.Custom_URC_Department;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomURCDepartmentService {

    private final CustomURCDepartmentRepository customURCDepartmentRepository;

    public List<Custom_URC_Department> findActiveDepartments() {
        return customURCDepartmentRepository.findByActiveTrueOrderByDisplayOrderAscDepartmentNameAsc();
    }
}

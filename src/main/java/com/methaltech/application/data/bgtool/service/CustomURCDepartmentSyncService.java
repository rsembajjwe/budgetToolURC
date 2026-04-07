package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.CustomURCDepartmentRepository;
import com.methaltech.application.data.entity.bgtool.Custom_URC_Department;
import com.methaltech.application.data.livedata.repository.UrcDepartmentAnlDimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomURCDepartmentSyncService {

    private final UrcDepartmentAnlDimRepository urcDepartmentAnlDimRepository;
    private final CustomURCDepartmentRepository customURCDepartmentRepository;

    @Transactional
    public void seedDefaultDepartments() {
        Map<String, String> selected = new LinkedHashMap<>();
        selected.put("D0003", "Civil Department");
        selected.put("D0002", "Mechanical Department");
        selected.put("S016", "Human Resource Section");
        selected.put("D0007", "MD Section");
        selected.put("S015", "Security Section");
        selected.put("D0008", "Internal Audit Department");
        selected.put("D0001", "Operations Department");
        selected.put("S014", "Corporate Planning Section");
        selected.put("D0006", "Finance Section");
        selected.put("S013", "Procurement Section");
        selected.put("S011", "Information Technology Section");
        selected.put("D0005", "Legal Department");

        int order = 1;

        for (Map.Entry<String, String> entry : selected.entrySet()) {
            String code = entry.getKey();
            String customName = entry.getValue();

            Custom_URC_Department dept = customURCDepartmentRepository.findById(code)
                    .orElseGet(Custom_URC_Department::new);

            dept.setDepartmentCode(code);
            dept.setDepartmentName(customName);
            dept.setSourceAnlCode(code);
            dept.setDisplayOrder(order++);
            dept.setActive(true);
            dept.setDepartmentType(resolveType(code));
            customURCDepartmentRepository.save(dept);
        }

        Custom_URC_Department notAnalysed = customURCDepartmentRepository.findById("#")
                .orElseGet(Custom_URC_Department::new);
        notAnalysed.setDepartmentCode("#");
        notAnalysed.setDepartmentName("Not Analysed");
        notAnalysed.setSourceAnlCode("#");
        notAnalysed.setDepartmentType("System");
        notAnalysed.setDisplayOrder(999);
        notAnalysed.setActive(true);
        notAnalysed.setDefaultSelection(true);
        customURCDepartmentRepository.save(notAnalysed);
    }

    private String resolveType(String code) {
        if (code == null) {
            return "Unknown";
        }
        if (code.startsWith("D")) {
            return "Department";
        }
        if (code.startsWith("S")) {
            return "Section";
        }
        return "Other";
    }
}

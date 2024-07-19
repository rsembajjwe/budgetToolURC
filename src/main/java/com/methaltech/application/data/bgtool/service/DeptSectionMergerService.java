package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.DepartmentRepository;
import com.methaltech.application.data.bgtool.repository.DeptSectionMergerRepository;
import com.methaltech.application.data.bgtool.repository.UrcDeptSectionAnlDimbgtRepository;
import com.methaltech.application.data.entity.bgtool.Department;
import com.methaltech.application.data.entity.bgtool.DeptSectionMerger;
import com.methaltech.application.data.entity.bgtool.UrDepartmentsAnlDim2;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.livedata.UrcDepartmentAnlDim;
import com.methaltech.application.data.livedata.repository.UrcDepartmentAnlDimRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeptSectionMergerService {

    private final DeptSectionMergerRepository repository;
    private final UrcDepartmentAnlDimRepository deptrepository;
    private final UrcDeptSectionAnlDimbgtRepository sectrepository;

    @Autowired
    public DeptSectionMergerService(DeptSectionMergerRepository repository, UrcDepartmentAnlDimRepository deptrepository,
            UrcDeptSectionAnlDimbgtRepository sectrepository) {
        this.repository = repository;
        this.deptrepository = deptrepository;
        this.sectrepository = sectrepository;
    }

    public List<DeptSectionMerger> getAllUrcDeptAndSections() {
        return repository.findAll();
    }
    public Optional<DeptSectionMerger> findBySectionCode(String sectioncode) {
        return repository.findBySectionCode2(sectioncode);
    }
    public String getDeptCode(String sectCode) {
        List<DeptSectionMerger> listAll = repository.findAll();
        Set<String> listesction = new HashSet<>();
        String deptCode = "";
        for (DeptSectionMerger h : listAll) {
            listesction.addAll(h.getSectioncodes());
            for (String c : h.getSectioncodes()) {
                if (c.trim().equals(sectCode.trim())) {
                    deptCode = h.getDeptcode();
                }
            }
        }
        return deptCode;
    }

    public List<UrcDepartmentAnlDim> getDeptCodes(List<UrcDeptSectionAnlDimbgt> sections) {
        // Extracting trimmed ANL_CODE values from sections
        List<String> trimmedANL_CODEList = sections.stream()
                .map(UrcDeptSectionAnlDimbgt::getANL_CODE) // Extract ANL_CODE
                .map(String::trim) // Trim the ANL_CODE
                .collect(Collectors.toList()); // Collect as a List
        List<DeptSectionMerger> listAll = repository.findAll();
        List<String> deptCode = new ArrayList<>();

        for (DeptSectionMerger h : listAll) {
            for (String c : h.getSectioncodes()) {
                if (trimmedANL_CODEList.contains(c.trim())) {
                    deptCode.add(h.getDeptcode());
                }
            }
        }
        //List<UrcDepartmentAnlDim> deptList =deptrepository.findByANL_CODEIn(deptCode);
        return deptrepository.findByANL_CODEIn(deptCode);
    }

    public Set<UrcDeptSectionAnlDimbgt> getSections(Set<UrDepartmentsAnlDim2> depts) {
        // Extracting trimmed ANL_CODE values from sections
        List<String> trimmedANL_CODEList = depts.stream()
                .map(UrDepartmentsAnlDim2::getAnlCode) // Extract ANL_CODE
                .map(String::trim) // Trim the ANL_CODE
                .collect(Collectors.toList()); // Collect as a List

        List<DeptSectionMerger> listAll = repository.findAll();
        Set<String> sectionCodes = new HashSet<>();

        for (DeptSectionMerger h : listAll) {
            for (String g : trimmedANL_CODEList) {
                if (g.trim().equals(h.getDeptcode().trim())) {
                    sectionCodes.addAll(h.getSectioncodes());
                }else{
                   
                }
            }

        }
        List<String> sectionCodes2 = sectionCodes.stream().map(String::trim).collect(Collectors.toList());
        return sectrepository.findByCustomANL_CODE2(sectionCodes2);
    }

    public Optional<DeptSectionMerger> findByDeptcodeCustom(String deptcode) {
        return repository.findByDeptcodeCustom(deptcode);
    }

    public DeptSectionMerger update(DeptSectionMerger entity) {
        return repository.save(entity);
    }

    public Optional<DeptSectionMerger> findByDeptcode(String deptcode) {
        return repository.findByDeptcode(deptcode);
    }

    public String findDepartment(String sectcode) {
        String deptString = repository.findDeptcodeBySectioncode(sectcode);
        UrcDepartmentAnlDim dept = deptrepository.findByANL_CODE(deptString);
        return dept.getNAME();
    }
        public String findDepartmentByDeptCode(String deptCode) {
        UrcDepartmentAnlDim dept = deptrepository.findByANL_CODE(deptCode);
        return dept.getNAME();
    }

    public String findDepartmentCode(String sectcode) {
        String deptString = repository.findDeptcodeBySectioncode(sectcode);
        return deptString;
    }

    public String findDeptcodeBySectioncode2(String sectcode) {
        Set<String> sectioncodes = Collections.singleton(sectcode);
        Optional<String> deptStringOptional = repository.findDeptcodeBySectioncode2(sectioncodes);
        return deptStringOptional.orElse(null); // or handle the absence of result in another way
    }

    public String findDeptcodeBySectioncode3(String sectcode) {
        List<String> sectcodes = new ArrayList<>();
        sectcodes.add(sectcode);
        List<DeptSectionMerger> deptSectionMergers = repository.findBySectioncodesIn(sectcodes);
        DeptSectionMerger merger = null;
        String dCode = "";
        if (!deptSectionMergers.isEmpty()) {
            merger = deptSectionMergers.get(0);
            dCode = merger.getDeptcode();
        }

        return dCode; // or handle the absence of result in another way
    }
}

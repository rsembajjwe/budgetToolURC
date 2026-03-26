package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.DepartmentRepository;
import com.methaltech.application.data.bgtool.repository.DeptSectionMergerRepository;
import com.methaltech.application.data.bgtool.repository.UrDepartmentsAnlDimRepository2;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeptSectionMergerService {
    
    private final DeptSectionMergerRepository repository;
    private final UrcDepartmentAnlDimRepository deptrepository;
    private final UrcDeptSectionAnlDimbgtRepository sectrepository;
    private UrDepartmentsAnlDimRepository2 departmentrepository;
    
    @Autowired
    public DeptSectionMergerService(DeptSectionMergerRepository repository, UrcDepartmentAnlDimRepository deptrepository,
            UrcDeptSectionAnlDimbgtRepository sectrepository, UrDepartmentsAnlDimRepository2 departmentrepository) {
        this.repository = repository;
        this.deptrepository = deptrepository;
        this.sectrepository = sectrepository;
        this.departmentrepository = departmentrepository;
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
    
    public UrcDepartmentAnlDim getDepartmentBySectionCode(String sectCode) {
        String deptcode = getDeptCode(sectCode);
        return findByANL_CODE(deptcode);
    }
    
    public UrcDepartmentAnlDim findByANL_CODE(String deptCode) {
        UrcDepartmentAnlDim dept = deptrepository.findByANL_CODE(deptCode);
        return dept;
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
                } else {
                    
                }
            }
            
        }
        List<String> sectionCodes2 = sectionCodes.stream().map(String::trim).collect(Collectors.toList());
        return sectrepository.findByCustomANL_CODE2(sectionCodes2);
    }
    
    public Set<UrcDeptSectionAnlDimbgt> getSectionsByDeptCode(String deptcode) {
        
        List<DeptSectionMerger> listAll = repository.findAll();
        Set<String> sectionCodes = new HashSet<>();
        
        for (DeptSectionMerger h : listAll) {
            
            if (deptcode.trim().equals(h.getDeptcode().trim())) {
                sectionCodes.addAll(h.getSectioncodes());
            } else {
                
            }
            
        }
        List<String> sectionCodes2 = sectionCodes.stream().map(String::trim).collect(Collectors.toList());
        // System.out.println(sectionCodes2 + " Section code");
        return sectrepository.findByCustomANL_CODE2(sectionCodes2);
    }
    
    public Set<UrcDeptSectionAnlDimbgt> getSectionsByDeptCode2(String deptcode) {
        List<String> sectionCodes2 = new ArrayList();
        String dept = "";

        //Not Analysis
        if (deptcode.equals("#              ")) {
            sectionCodes2.add("#              ");
            dept = "Not Analysed";
        } //Civil
        else if (deptcode.equals("D0003")) {
            sectionCodes2.add("S004");
            sectionCodes2.add("S005");
            dept = "Civil";
        } //Mechanical
        else if (deptcode.equals("D0002")) {
            sectionCodes2.add("S002");
            sectionCodes2.add("S003");
            dept = "Mechanical";
        } //Human Resource
        else if (deptcode.equals("S016")) {
            sectionCodes2.add("S016");
            dept = "Human Resource";
        } //MD
        else if (deptcode.equals("D0007")) {
            sectionCodes2.add("S017");
            dept = "MD";
        } //Security
        else if (deptcode.equals("S015")) {
            sectionCodes2.add("S015");
            dept = "Security";
        } //Internal Audit
        else if (deptcode.equals("D0008")) {
            sectionCodes2.add("S018");
            dept = "Internal Audit";
        } //Commercial Operations
        else if (deptcode.equals("D0001")) {
            
            sectionCodes2.add("S001");
            sectionCodes2.add("S006");
            sectionCodes2.add("S020");
            dept = "Commercial Operations";
        } //Corporate Planning
        else if (deptcode.equals("S014")) {
            sectionCodes2.add("S014");
            dept = "Corporate Planning";
        } //Finance
        else if (deptcode.equals("D0006")) {
            sectionCodes2.add("S009");
            sectionCodes2.add("S010");
            sectionCodes2.add("S012");
            sectionCodes2.add("S021");
            dept = "Finance";
        } //Procurement
        else if (deptcode.equals("S013")) {
            sectionCodes2.add("S013");
            dept = "Procurement";
        } //Information Technology
        else if (deptcode.equals("S011")) {
            sectionCodes2.add("S011");
            dept = "Information Technology";
        } //Legal
        else if (deptcode.equals("D0005")) {
            sectionCodes2.add("S007");
            sectionCodes2.add("S008");
            dept = "Legal";
        }
        Set<UrcDeptSectionAnlDimbgt> sections = sectrepository.findByCustomANL_CODE2(sectionCodes2);
        return sections;
    }
    
    public Set<String> extractSectionAnlCodes(String deptcode) {
        Set<UrcDeptSectionAnlDimbgt> entities = getSectionsByDeptCode(deptcode);
        if (entities == null || entities.isEmpty()) {
            return Set.of(); // return empty immutable set
        }
        return entities.stream()
                .map(UrcDeptSectionAnlDimbgt::getANL_CODE) // extract ANL_CODE
                .filter(code -> code != null && !code.isBlank()) // optional: avoid null/blank
                .collect(Collectors.toSet());
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
    
    public UrcDepartmentAnlDim findDepartmentDetail(String sectcode) {
        String deptString = repository.findDeptcodeBySectioncode(sectcode);
        return deptrepository.findByANL_CODE(deptString);
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
    
    @Transactional(readOnly = true)
    public String getDeptCodeBySectionCode(String sectionCode) {
        return repository.findDeptCodeBySectionCode(sectionCode);
    }
    
    public Optional<UrDepartmentsAnlDim2> getDepartmentByAnlCode(String anlCode) {
        return departmentrepository.findByAnlCode(anlCode);
    }
    
    public Set<UrcDeptSectionAnlDimbgt> getByAnlCodes(Set<String> anlCodes) {
        
        if (anlCodes == null || anlCodes.isEmpty()) {
            return Collections.emptySet();
        }

        // Optional: normalize to match DB (trim/pad if needed)
        Set<String> normalizedCodes = anlCodes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.toSet());
        
        return sectrepository.findByAnlCodeIn(normalizedCodes);
    }
}

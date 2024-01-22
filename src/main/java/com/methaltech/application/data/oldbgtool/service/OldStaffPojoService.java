
package com.methaltech.application.data.oldbgtool.service;

import com.methaltech.application.data.entity.oldbgtool.OldStaffPojo;
import com.methaltech.application.data.oldbgtool.repository.OldStaffPojoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OldStaffPojoService {

    private final OldStaffPojoRepository staffRepository;

    @Autowired
    public OldStaffPojoService(OldStaffPojoRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public OldStaffPojo saveStaff(OldStaffPojo staff) {
        return staffRepository.save(staff);
    }

    public List<OldStaffPojo> getAllStaff() {
        return staffRepository.findAll();
    }

    public OldStaffPojo getStaffById(Integer id) {
        return staffRepository.findById(id).orElse(null);
    }

    public List<OldStaffPojo> getStaffByFiscalYear(String fiscalYear) {
        return staffRepository.findByFiscalYear(fiscalYear);
    }

    public void deleteStaff(Integer id) {
        staffRepository.deleteById(id);
    }
}

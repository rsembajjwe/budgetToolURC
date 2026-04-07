
package com.methaltech.application.data.bgtool.repository;
import com.methaltech.application.data.entity.bgtool.URC_Programme_Logframe_Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface URCProgrammeLogframeItemRepository extends JpaRepository<URC_Programme_Logframe_Item, Long> {
    List<URC_Programme_Logframe_Item> findByProgrammeIdOrderByDisplayOrderAsc(Long programmeId);
    List<URC_Programme_Logframe_Item> findByDepartmentDepartmentCode(String departmentCode);
}

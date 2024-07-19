
package com.methaltech.application.data.oldbgtool.repository;

import com.methaltech.application.data.entity.oldbgtool.OldItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OldItemRepository extends JpaRepository<OldItem, Integer> {
    OldItem findById(int id);
    OldItem findByCode(String code);
}

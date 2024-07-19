
package com.methaltech.application.data.oldbgtool.service;
import com.methaltech.application.data.entity.oldbgtool.OldItem;
import com.methaltech.application.data.oldbgtool.repository.OldItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OldItemService {
    private final OldItemRepository oldItemRepository;

    @Autowired
    public OldItemService(OldItemRepository oldItemRepository) {
        this.oldItemRepository = oldItemRepository;
    }

    public OldItem findItemById(int id) {
        return oldItemRepository.findById(id);
    }

    public OldItem findItemByCode(String code) {
        return oldItemRepository.findByCode(code);
    }
}


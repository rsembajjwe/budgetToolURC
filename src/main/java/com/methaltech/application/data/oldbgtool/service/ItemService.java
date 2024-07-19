package com.methaltech.application.data.oldbgtool.service;


import com.methaltech.application.data.entity.oldbgtool.Item;
import com.methaltech.application.data.oldbgtool.repository.ItemRepository;
import com.methaltech.application.data.livedata.repository.UR5_ACNTRepository;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.methaltech.application.data.entity.livedata.UR5_ACNT;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UR5_ACNTRepository ur5_acntRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, UR5_ACNTRepository ur5_acntRepository) {
        this.itemRepository = itemRepository;
        this.ur5_acntRepository = ur5_acntRepository;
    }

    public void updateItemsBasedOnUR5_ACNT() {
        int del = 0;
        int up = 0;
        int sa = 1;
        // Retrieve UR5_ACNT entities
        List<UR5_ACNT> ur5AcntList = ur5_acntRepository.findByAcntCodeStartingWithAndAcntCodeGreaterThanAndAcntCodeLessThan();


        // Create a map for quick lookup by acntCode
        Map<String, UR5_ACNT> ur5AcntMap = ur5AcntList.stream()
                .collect(Collectors.toMap(UR5_ACNT::getAcntCode, ur5Acnt -> ur5Acnt));

        // Retrieve existing Items
        List<Item> existingItems = itemRepository.findAll();

        for (UR5_ACNT ur5Acnt : ur5AcntList) {
            String acntCode = ur5Acnt.getAcntCode();
            String descr = ur5Acnt.getDescr();

            // Check if acntCode exists in existing Items
            Item existingItem = existingItems.stream()
                    .filter(item -> acntCode.trim().equals(item.getCode()))
                    .findFirst()
                    .orElse(null);
            //

            if (existingItem != null) {
                // Update existing Item
                existingItem.setName(descr);

                itemRepository.save(existingItem);
                up++;

            } else {
                // Create a new Item if acntCode is not found
                Item newItem = new Item();
                newItem.setCode(acntCode);
                newItem.setName(descr);
                // Set other properties as needed
                // ...

                itemRepository.save(newItem);
                //sa++;
                //System.out.println(newItem + "  " + sa++);
            }
        }
        // Create a set of acntCodes from UR5_ACNT entities
        Set<String> ur5AcntCodes = ur5AcntList.stream()
                .map(ur5Acnt -> ur5Acnt.getAcntCode().trim())
                .collect(Collectors.toSet());
        // Create a list to collect items to delete
        List<Item> itemsToDelete = new ArrayList<>();
        for (Item item : existingItems) {
            String code = item.getCode();

            if (ur5AcntCodes.contains(code)) {
                // Item exists in UR5_ACNT, update it
                /*                UR5_ACNT ur5Acnt = ur5AcntMap.get(code);
                item.setName(ur5Acnt.getDescr());
                itemRepository.save(item);*/
            } else {
                // Item does not exist in UR5_ACNT, mark it for deletion
                itemsToDelete.add(item);
                del++;
            }
        }

        // Delete items that do not exist in UR5_ACNT
        itemRepository.deleteAll(itemsToDelete);
    }
}


package com.methaltech.application.data.oldbgtool.service;
import com.methaltech.application.data.entity.oldbgtool.Item;
import com.methaltech.application.data.oldbgtool.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService2 {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemService2(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemById(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + id));
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public void deleteItem(int id) {
        itemRepository.deleteById(id);
    }
}


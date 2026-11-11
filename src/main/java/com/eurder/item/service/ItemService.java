package com.eurder.item.service;

import com.eurder.item.domain.Item;
import com.eurder.item.domain.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional
    public Item addItem(Item item) {
        Item addedItem = itemRepository.save(item);
        log.info("Item added with id {}", addedItem.getId());
        return addedItem;
    }
}
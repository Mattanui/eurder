package com.eurder.item.service;

import com.eurder.PersistenceTest;
import com.eurder.item.domain.Item;
import com.eurder.item.domain.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.eurder.item.domain.ItemTestData.anItem;
import static org.assertj.core.api.Assertions.assertThat;

@PersistenceTest
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void addItem_savesAndReturnsTheItem() {
        Item item = anItem();

        Item addedItem = itemService.addItem(item);

        assertThat(addedItem).isEqualTo(item);
        assertThat(itemRepository.findById(item.getId())).contains(item);
    }
}
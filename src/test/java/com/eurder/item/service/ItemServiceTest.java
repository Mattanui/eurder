package com.eurder.item.service;

import com.eurder.item.domain.Item;
import com.eurder.item.domain.ItemRepository;
import org.junit.jupiter.api.Test;

import static com.eurder.item.domain.ItemTestData.anItem;
import static org.assertj.core.api.Assertions.assertThat;

class ItemServiceTest {

    private final ItemRepository itemRepository = new ItemRepository();
    private final ItemService itemService = new ItemService(itemRepository);

    @Test
    void addItem_savesAndReturnsTheItem() {
        Item item = anItem();

        Item addedItem = itemService.addItem(item);

        assertThat(addedItem).isSameAs(item);
        assertThat(itemRepository.findById(item.getId())).containsSame(item);
    }
}
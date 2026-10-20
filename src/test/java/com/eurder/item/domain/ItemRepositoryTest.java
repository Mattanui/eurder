package com.eurder.item.domain;

import org.junit.jupiter.api.Test;
import static com.eurder.item.domain.ItemTestData.anItem;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRepositoryTest {

    private final ItemRepository repository = new ItemRepository();

    @Test
    void save_thenFindById_returnsTheSavedItem() {
        Item item = anItem();

        repository.save(item);

        assertThat(repository.findById(item.getId())).containsSame(item);
    }

    @Test
    void findById_withUnknownId_returnsEmpty() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }

}
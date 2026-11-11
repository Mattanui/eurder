package com.eurder.item.domain;

import com.eurder.PersistenceTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.UUID;

import static com.eurder.item.domain.ItemTestData.anItem;
import static org.assertj.core.api.Assertions.assertThat;

@PersistenceTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void save_thenFindById_readsTheItemBackFromTheDatabase() {
        Item item = repository.save(anItem());
        entityManager.flush();
        entityManager.clear();

        Item found = repository.findById(item.getId()).orElseThrow();

        assertThat(found).isNotSameAs(item);
        assertThat(found).isEqualTo(item);
        assertThat(found.getName()).isEqualTo("Fender Stratocaster");
        assertThat(found.getDescription()).isEqualTo("Electric guitar with a maple neck");
        assertThat(found.getPrice()).isEqualTo(new Price(new BigDecimal("699.95")));
        assertThat(found.getStock()).isEqualTo(12);
    }

    @Test
    void findById_withUnknownId_returnsEmpty() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    void aStockChangeOnAManagedItem_isWrittenWithoutCallingSave() {
        Item item = repository.save(anItem());
        entityManager.flush();

        item.decreaseStock(5);
        entityManager.flush();
        entityManager.clear();

        assertThat(repository.findById(item.getId()).orElseThrow().getStock()).isEqualTo(7);
    }
}
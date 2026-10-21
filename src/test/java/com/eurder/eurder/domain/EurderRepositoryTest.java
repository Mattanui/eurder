package com.eurder.eurder.domain;

import com.eurder.item.domain.Price;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EurderRepositoryTest {

    private final EurderRepository repository = new EurderRepository();

    @Test
    void save_thenFindById_returnsTheSavedEurder() {
        Eurder eurder = anEurder();

        repository.save(eurder);

        assertThat(repository.findById(eurder.getId())).containsSame(eurder);
    }

    @Test
    void findById_withUnknownId_returnsEmpty() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }

    private static Eurder anEurder() {
        LocalDate orderDate = LocalDate.of(2026, 10, 21);
        ItemGroup itemGroup = new ItemGroup(UUID.randomUUID(), "Fender Stratocaster",
                new Price(new BigDecimal("699.95")), 1, orderDate.plusDays(1));
        return new Eurder(UUID.randomUUID(), orderDate, List.of(itemGroup));
    }
}
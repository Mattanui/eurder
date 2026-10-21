package com.eurder.eurder.service;

import com.eurder.eurder.domain.Eurder;
import com.eurder.eurder.domain.EurderRepository;
import com.eurder.item.domain.Item;
import com.eurder.item.domain.ItemRepository;
import com.eurder.item.domain.Price;
import com.eurder.shared.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EurderServiceTest {

    private static final ZoneId BRUSSELS = ZoneId.of("Europe/Brussels");
    private static final Clock OCTOBER_21_AT_NOON = Clock.fixed(Instant.parse("2026-10-21T10:00:00Z"), BRUSSELS);
    private static final UUID CUSTOMER_ID = UUID.randomUUID();

    private final ItemRepository itemRepository = new ItemRepository();
    private final EurderRepository eurderRepository = new EurderRepository();
    private final EurderService eurderService =
            new EurderService(eurderRepository, itemRepository, OCTOBER_21_AT_NOON);

    private Item guitar;
    private Item strap;

    @BeforeEach
    void setUp() {
        guitar = itemRepository.save(new Item("Fender Stratocaster", "Electric guitar",
                new Price(new BigDecimal("699.95")), 10));
        strap = itemRepository.save(new Item("Leather strap", "Brown leather strap",
                new Price(new BigDecimal("49.99")), 1));
    }

    @Test
    void placeEurder_computesOrderAndShippingDatesFromTheClock() {
        Eurder eurder = eurderService.placeEurder(CUSTOMER_ID, List.of(
                new ItemGroupRequest(guitar.getId(), 2),
                new ItemGroupRequest(strap.getId(), 3)));

        assertThat(eurder.getOrderDate()).isEqualTo(LocalDate.of(2026, 10, 21));
        assertThat(eurder.getItemGroups().get(0).shippingDate()).isEqualTo(LocalDate.of(2026, 10, 22));
        assertThat(eurder.getItemGroups().get(1).shippingDate()).isEqualTo(LocalDate.of(2026, 10, 28));
    }

    @Test
    void placeEurder_savesTheEurderForTheCustomer() {
        Eurder eurder = eurderService.placeEurder(CUSTOMER_ID, List.of(new ItemGroupRequest(guitar.getId(), 1)));

        assertThat(eurder.getCustomerId()).isEqualTo(CUSTOMER_ID);
        assertThat(eurderRepository.findById(eurder.getId())).containsSame(eurder);
    }

    @Test
    void placeEurder_decreasesTheStockOfTheOrderedItems() {
        eurderService.placeEurder(CUSTOMER_ID, List.of(
                new ItemGroupRequest(guitar.getId(), 2),
                new ItemGroupRequest(strap.getId(), 3)));

        assertThat(guitar.getStock()).isEqualTo(8);
        assertThat(strap.getStock()).isZero();
    }

    @Test
    void placeEurder_withAnUnknownItem_isRejected_andChangesNoStock() {
        UUID unknownItemId = UUID.randomUUID();

        assertThatThrownBy(() -> eurderService.placeEurder(CUSTOMER_ID, List.of(
                new ItemGroupRequest(guitar.getId(), 2),
                new ItemGroupRequest(unknownItemId, 1))))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Item not found: " + unknownItemId);
        assertThat(guitar.getStock()).isEqualTo(10);
    }

    @Test
    void placeEurder_withTheSameItemTwice_isRejected_andChangesNoStock() {
        assertThatThrownBy(() -> eurderService.placeEurder(CUSTOMER_ID, List.of(
                new ItemGroupRequest(guitar.getId(), 1),
                new ItemGroupRequest(guitar.getId(), 2))))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Item " + guitar.getId() + " appears more than once in the order");
        assertThat(guitar.getStock()).isEqualTo(10);
    }

    @Test
    void placeEurder_withAnInvalidAmount_isRejected_andChangesNoStock() {
        assertThatThrownBy(() -> eurderService.placeEurder(CUSTOMER_ID, List.of(
                new ItemGroupRequest(guitar.getId(), 2),
                new ItemGroupRequest(strap.getId(), 0))))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Amount must be at least 1");
        assertThat(guitar.getStock()).isEqualTo(10);
    }

    @Test
    void placeEurder_justAfterMidnightInBrussels_usesTheBrusselsDate() {
        // 22:30 UTC on October 21 is already 00:30 on October 22 in Brussels (UTC+2 in October)
        Clock justAfterMidnightInBrussels = Clock.fixed(Instant.parse("2026-10-21T22:30:00Z"), BRUSSELS);
        EurderService service = new EurderService(eurderRepository, itemRepository, justAfterMidnightInBrussels);

        Eurder eurder = service.placeEurder(CUSTOMER_ID, List.of(new ItemGroupRequest(guitar.getId(), 1)));

        assertThat(eurder.getOrderDate()).isEqualTo(LocalDate.of(2026, 10, 22));
    }
}
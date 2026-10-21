package com.eurder.eurder.domain;

import com.eurder.item.domain.Price;
import com.eurder.shared.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EurderTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final LocalDate ORDER_DATE = LocalDate.of(2026, 10, 21);

    private static final ItemGroup GUITARS = new ItemGroup(UUID.randomUUID(), "Fender Stratocaster",
            new Price(new BigDecimal("699.95")), 2, ORDER_DATE.plusDays(1));
    private static final ItemGroup STRAPS = new ItemGroup(UUID.randomUUID(), "Leather strap",
            new Price(new BigDecimal("49.99")), 3, ORDER_DATE.plusDays(7));

    @Test
    void createEurder_keepsCustomerOrderDateAndItemGroups() {
        Eurder eurder = new Eurder(CUSTOMER_ID, ORDER_DATE, List.of(GUITARS, STRAPS));

        assertThat(eurder.getCustomerId()).isEqualTo(CUSTOMER_ID);
        assertThat(eurder.getOrderDate()).isEqualTo(ORDER_DATE);
        assertThat(eurder.getItemGroups()).containsExactly(GUITARS, STRAPS);
    }

    @Test
    void createEurder_receivesAGeneratedId() {
        Eurder eurder = new Eurder(CUSTOMER_ID, ORDER_DATE, List.of(GUITARS));

        assertThat(eurder.getId()).isNotNull();
    }

    @Test
    void totalPrice_isTheSumOfTheItemGroupTotals() {
        Eurder eurder = new Eurder(CUSTOMER_ID, ORDER_DATE, List.of(GUITARS, STRAPS));

        assertThat(eurder.getTotalPrice()).isEqualTo(new Price(new BigDecimal("1549.87")));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void createEurder_withoutItemGroups_isRejected(List<ItemGroup> missingItemGroups) {
        assertThatThrownBy(() -> new Eurder(CUSTOMER_ID, ORDER_DATE, missingItemGroups))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("An order must contain at least one item group");
    }

    @Test
    void createEurder_withTheSameItemTwice_isRejected() {
        UUID itemId = UUID.randomUUID();
        ItemGroup first = new ItemGroup(itemId, "Fender Stratocaster",
                new Price(new BigDecimal("699.95")), 1, ORDER_DATE.plusDays(1));
        ItemGroup second = new ItemGroup(itemId, "Fender Stratocaster",
                new Price(new BigDecimal("699.95")), 2, ORDER_DATE.plusDays(1));

        assertThatThrownBy(() -> new Eurder(CUSTOMER_ID, ORDER_DATE, List.of(first, second)))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Item " + itemId + " appears more than once in the order");
    }

    @Test
    void itemGroups_areNotAffectedByChangesToTheOriginalList() {
        List<ItemGroup> originalList = new ArrayList<>(List.of(GUITARS, STRAPS));
        Eurder eurder = new Eurder(CUSTOMER_ID, ORDER_DATE, originalList);

        originalList.clear();

        assertThat(eurder.getItemGroups()).containsExactly(GUITARS, STRAPS);
    }

    @Test
    void itemGroups_cannotBeModifiedFromOutside() {
        Eurder eurder = new Eurder(CUSTOMER_ID, ORDER_DATE, List.of(GUITARS));

        assertThatThrownBy(() -> eurder.getItemGroups().add(STRAPS))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void createEurder_withoutCustomerId_isRejected() {
        assertThatThrownBy(() -> new Eurder(null, ORDER_DATE, List.of(GUITARS)))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Customer id must be provided");
    }

    @Test
    void createEurder_withoutOrderDate_isRejected() {
        assertThatThrownBy(() -> new Eurder(CUSTOMER_ID, null, List.of(GUITARS)))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Order date must be provided");
    }
}
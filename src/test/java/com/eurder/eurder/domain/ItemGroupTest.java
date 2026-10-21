package com.eurder.eurder.domain;

import com.eurder.item.domain.Item;
import com.eurder.item.domain.Price;
import com.eurder.shared.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.eurder.item.domain.ItemTestData.anItemWithStock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemGroupTest {

    private static final LocalDate ORDER_DATE = LocalDate.of(2026, 10, 21);

    @Test
    void of_withEnoughStock_shipsTheNextDay() {
        ItemGroup itemGroup = ItemGroup.of(anItemWithStock(10), 2, ORDER_DATE);

        assertThat(itemGroup.shippingDate()).isEqualTo(LocalDate.of(2026, 10, 22));
    }

    @Test
    void of_withExactlyTheStock_shipsTheNextDay() {
        ItemGroup itemGroup = ItemGroup.of(anItemWithStock(10), 10, ORDER_DATE);

        assertThat(itemGroup.shippingDate()).isEqualTo(LocalDate.of(2026, 10, 22));
    }

    @Test
    void of_withInsufficientStock_shipsNextWeek() {
        ItemGroup itemGroup = ItemGroup.of(anItemWithStock(1), 3, ORDER_DATE);

        assertThat(itemGroup.shippingDate()).isEqualTo(LocalDate.of(2026, 10, 28));
    }

    @Test
    void of_copiesTheItemIdNameAndCurrentPrice() {
        Item item = anItemWithStock(10);

        ItemGroup itemGroup = ItemGroup.of(item, 2, ORDER_DATE);

        assertThat(itemGroup)
                .extracting(ItemGroup::itemId, ItemGroup::itemName, ItemGroup::unitPrice, ItemGroup::amount)
                .containsExactly(item.getId(), "Fender Stratocaster", new Price(new BigDecimal("699.95")), 2);
    }

    @Test
    void of_doesNotChangeTheStock() {
        Item item = anItemWithStock(10);

        ItemGroup.of(item, 2, ORDER_DATE);

        assertThat(item.getStock()).isEqualTo(10);
    }

    @Test
    void of_withoutAmount_isRejected() {
        assertThatThrownBy(() -> ItemGroup.of(anItemWithStock(10), null, ORDER_DATE))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Amount must be provided");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void of_withAnAmountBelowOne_isRejected(int amount) {
        assertThatThrownBy(() -> ItemGroup.of(anItemWithStock(10), amount, ORDER_DATE))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Amount must be at least 1");
    }

    @Test
    void totalPrice_isTheUnitPriceTimesTheAmount() {
        ItemGroup itemGroup = ItemGroup.of(anItemWithStock(10), 2, ORDER_DATE);

        assertThat(itemGroup.totalPrice()).isEqualTo(new Price(new BigDecimal("1399.90")));
    }
}
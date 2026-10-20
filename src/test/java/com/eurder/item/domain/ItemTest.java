package com.eurder.item.domain;

import com.eurder.shared.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemTest {

    private static final String NAME = "Fender Stratocaster";
    private static final String DESCRIPTION = "Electric guitar with a maple neck";
    private static final Price PRICE = new Price(new BigDecimal("699.95"));

    @Test
    void createItem_withValidData_keepsTheData() {
        Item item = new Item(NAME, DESCRIPTION, PRICE, 12);

        assertThat(item)
                .extracting(Item::getName, Item::getDescription, Item::getPrice, Item::getStock)
                .containsExactly(NAME, DESCRIPTION, PRICE, 12);
    }

    @Test
    void createItem_receivesAGeneratedId() {
        Item item = new Item(NAME, DESCRIPTION, PRICE, 12);

        assertThat(item.getId()).isNotNull();
    }

    @Test
    void createItem_withSurroundingSpaces_isTrimmed() {
        Item item = new Item("  Fender Stratocaster ", " Electric guitar with a maple neck  ", PRICE, 12);

        assertThat(item)
                .extracting(Item::getName, Item::getDescription)
                .containsExactly(NAME, DESCRIPTION);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void createItem_withoutName_isRejected(String missingValue) {
        assertThatThrownBy(() -> new Item(missingValue, DESCRIPTION, PRICE, 12))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Name must be provided");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void createItem_withoutDescription_isRejected(String missingValue) {
        assertThatThrownBy(() -> new Item(NAME, missingValue, PRICE, 12))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Description must be provided");
    }

    @Test
    void createItem_withDescriptionOfExactly255Characters_isAccepted() {
        String description = "a".repeat(255);

        Item item = new Item(NAME, description, PRICE, 12);

        assertThat(item.getDescription()).isEqualTo(description);
    }

    @Test
    void createItem_withDescriptionLongerThan255Characters_isRejected() {
        assertThatThrownBy(() -> new Item(NAME, "a".repeat(256), PRICE, 12))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Description must not exceed 255 characters");
    }

    @Test
    void createItem_withoutPrice_isRejected() {
        assertThatThrownBy(() -> new Item(NAME, DESCRIPTION, null, 12))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Price must be provided");
    }

    @Test
    void createItem_withoutStock_isRejected() {
        assertThatThrownBy(() -> new Item(NAME, DESCRIPTION, PRICE, null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Stock must be provided");
    }

    @Test
    void createItem_withZeroStock_isAccepted() {
        Item item = new Item(NAME, DESCRIPTION, PRICE, 0);

        assertThat(item.getStock()).isZero();
    }

    @Test
    void createItem_withNegativeStock_isRejected() {
        assertThatThrownBy(() -> new Item(NAME, DESCRIPTION, PRICE, -1))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Stock must not be negative");
    }
}
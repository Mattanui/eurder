package com.eurder.item.api;

import com.eurder.item.api.dto.CreateItemDto;
import com.eurder.item.api.dto.ItemDto;
import com.eurder.item.domain.Item;
import com.eurder.item.domain.Price;
import com.eurder.shared.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.eurder.item.domain.ItemTestData.anItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemMapperTest {

    private final ItemMapper mapper = new ItemMapper();

    @Test
    void toItem_mapsAllFields() {
        CreateItemDto dto = new CreateItemDto("Fender Stratocaster", "Electric guitar with a maple neck",
                new BigDecimal("699.95"), 12);

        Item item = mapper.toItem(dto);

        assertThat(item)
                .extracting(Item::getName, Item::getDescription, Item::getPrice, Item::getStock)
                .containsExactly("Fender Stratocaster", "Electric guitar with a maple neck",
                        new Price(new BigDecimal("699.95")), 12);
    }

    @Test
    void toItem_withoutPrice_isRejected() {
        CreateItemDto dto = new CreateItemDto("Fender Stratocaster", "Electric guitar", null, 12);

        assertThatThrownBy(() -> mapper.toItem(dto))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Price must be provided");
    }

    @Test
    void toItem_withoutStock_isRejected() {
        CreateItemDto dto = new CreateItemDto("Fender Stratocaster", "Electric guitar",
                new BigDecimal("699.95"), null);

        assertThatThrownBy(() -> mapper.toItem(dto))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Stock must be provided");
    }

    @Test
    void toDto_mapsAllFields() {
        Item item = anItem();

        ItemDto dto = mapper.toDto(item);

        assertThat(dto).isEqualTo(new ItemDto(item.getId(), "Fender Stratocaster",
                "Electric guitar with a maple neck", new BigDecimal("699.95"), 12));
    }
}
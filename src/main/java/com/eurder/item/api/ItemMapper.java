package com.eurder.item.api;

import com.eurder.item.api.dto.CreateItemDto;
import com.eurder.item.api.dto.ItemDto;
import com.eurder.item.domain.Item;
import com.eurder.item.domain.Price;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public Item toItem(CreateItemDto dto) {
        return new Item(dto.name(), dto.description(), new Price(dto.price()), dto.stock());
    }

    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getPrice().amount(), item.getStock());
    }
}
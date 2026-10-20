package com.eurder.item.domain;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ItemRepository {

    private final Map<UUID, Item> itemsById = new ConcurrentHashMap<>();

    public Item save(Item item) {
        itemsById.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findById(UUID id) {
        return Optional.ofNullable(itemsById.get(id));
    }
}
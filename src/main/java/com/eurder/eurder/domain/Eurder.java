package com.eurder.eurder.domain;

import com.eurder.item.domain.Price;
import com.eurder.shared.InvalidInputException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.eurder.shared.Validation.requireValue;

public class Eurder {

    private final UUID id;
    private final UUID customerId;
    private final LocalDate orderDate;
    private final List<ItemGroup> itemGroups;

    public Eurder(UUID customerId, LocalDate orderDate, List<ItemGroup> itemGroups) {
        this.id = UUID.randomUUID();
        this.customerId = requireValue(customerId, "Customer id");
        this.orderDate = requireValue(orderDate, "Order date");
        this.itemGroups = validateItemGroups(itemGroups);
    }

    private static List<ItemGroup> validateItemGroups(List<ItemGroup> itemGroups) {
        if (itemGroups == null || itemGroups.isEmpty()) {
            throw new InvalidInputException("An order must contain at least one item group");
        }
        Set<UUID> orderedItemIds = new HashSet<>();
        for (ItemGroup itemGroup : itemGroups) {
            if (!orderedItemIds.add(itemGroup.itemId())) {
                throw new InvalidInputException(
                        "Item " + itemGroup.itemId() + " appears more than once in the order");
            }
        }
        return List.copyOf(itemGroups);
    }

    public Price getTotalPrice() {
        return itemGroups.stream()
                .map(ItemGroup::totalPrice)
                .reduce(Price.ZERO, Price::add);
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public List<ItemGroup> getItemGroups() {
        return itemGroups;
    }
}
package com.eurder.eurder.domain;

import com.eurder.customer.domain.Customer;
import com.eurder.item.domain.Price;
import com.eurder.shared.InvalidInputException;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;

import static com.eurder.shared.Validation.requireValue;

@Entity
@Table(name = "eurder")
public class Eurder {

    @Id
    private UUID id;

    private UUID customerId;

    private LocalDate orderDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "item_group", joinColumns = @JoinColumn(name = "eurder_id"))
    @OrderColumn(name = "line_number")
    @AttributeOverride(name = "unitPrice.amount", column = @Column(name = "unit_price"))
    private List<ItemGroup> itemGroups = new ArrayList<>();

    protected Eurder() {
    }

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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof Eurder that && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
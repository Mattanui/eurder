package com.eurder.eurder.domain;

import com.eurder.item.domain.Item;
import com.eurder.item.domain.Price;
import com.eurder.shared.InvalidInputException;

import java.time.LocalDate;
import java.util.UUID;

import static com.eurder.shared.Validation.requireValue;

public record ItemGroup(UUID itemId, String itemName, Price unitPrice, int amount, LocalDate shippingDate) {

    private static final int DAYS_TO_SHIP_WHEN_IN_STOCK = 1;
    private static final int DAYS_TO_SHIP_WHEN_OUT_OF_STOCK = 7;

    public ItemGroup {
        if (amount < 1) {
            throw new InvalidInputException("Amount must be at least 1");
        }
    }

    public static ItemGroup of(Item item, Integer amount, LocalDate orderDate) {
        requireValue(amount, "Amount");
        int daysToShip = item.isInStock(amount) ? DAYS_TO_SHIP_WHEN_IN_STOCK : DAYS_TO_SHIP_WHEN_OUT_OF_STOCK;
        return new ItemGroup(item.getId(), item.getName(), item.getPrice(), amount, orderDate.plusDays(daysToShip));
    }

    public Price totalPrice() {
        return unitPrice.multiply(amount);
    }
}
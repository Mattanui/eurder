package com.eurder.item.domain;

import com.eurder.shared.InvalidInputException;

import java.util.UUID;

import static com.eurder.shared.Validation.requireText;
import static com.eurder.shared.Validation.requireValue;

public class Item {

    private static final int MAX_DESCRIPTION_LENGTH = 255;

    private final UUID id;
    private final String name;
    private final String description;
    private final Price price;
    private final int stock;

    public Item(String name, String description, Price price, Integer stock) {
        this.id = UUID.randomUUID();
        this.name = requireText(name, "Name");
        this.description = validateDescription(description);
        this.price = requireValue(price, "Price");
        this.stock = validateStock(stock);
    }

    private static String validateDescription(String description) {
        String trimmedDescription = requireText(description, "Description");
        if (trimmedDescription.length() > MAX_DESCRIPTION_LENGTH) {
            throw new InvalidInputException(
                    "Description must not exceed " + MAX_DESCRIPTION_LENGTH + " characters");
        }
        return trimmedDescription;
    }

    private static int validateStock(Integer stock) {
        requireValue(stock, "Stock");
        if (stock < 0) {
            throw new InvalidInputException("Stock must not be negative");
        }
        return stock;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Price getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }
}
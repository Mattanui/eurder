package com.eurder.item.domain;

import com.eurder.customer.domain.Customer;
import com.eurder.shared.InvalidInputException;
import jakarta.persistence.*;

import java.util.UUID;

import static com.eurder.shared.Validation.requireText;
import static com.eurder.shared.Validation.requireValue;

@Entity
@Table(name = "item")
public class Item {

    private static final int MAX_DESCRIPTION_LENGTH = 255;

    @Id
    private UUID id;

    private String name;

    private String description;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "price"))
    private Price price;

    private int stock;

    @Version
    private Long version;

    protected Item() {
    }

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

    public boolean isInStock(int requestedAmount) {
        return stock >= requestedAmount;
    }

    public void decreaseStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to decrease must be positive, but was " + amount);
        }
        stock = Math.max(0, stock - amount);
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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof Item that && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
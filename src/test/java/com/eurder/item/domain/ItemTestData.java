package com.eurder.item.domain;

import java.math.BigDecimal;

public final class ItemTestData {

    private ItemTestData() {
    }

    public static Item anItem() {
        return new Item("Fender Stratocaster", "Electric guitar with a maple neck",
                new Price(new BigDecimal("699.95")), 12);
    }

    public static Item anItemWithStock(int stock) {
        return new Item("Fender Stratocaster", "Electric guitar with a maple neck",
                new Price(new BigDecimal("699.95")), stock);
    }
}
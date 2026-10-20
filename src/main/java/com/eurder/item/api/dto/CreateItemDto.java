package com.eurder.item.api.dto;

import java.math.BigDecimal;

public record CreateItemDto(String name, String description, BigDecimal price, Integer stock) {
}
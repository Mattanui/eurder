package com.eurder.eurder.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ItemGroupDto(UUID itemId, String itemName, BigDecimal unitPrice, int amount,
                           LocalDate shippingDate, BigDecimal totalPrice) {
}
package com.eurder.eurder.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record EurderDto(UUID id, UUID customerId, LocalDate orderDate,
                        List<ItemGroupDto> itemGroups, BigDecimal totalPrice) {
}
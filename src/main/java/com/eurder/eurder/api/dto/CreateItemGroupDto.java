package com.eurder.eurder.api.dto;

import java.util.UUID;

public record CreateItemGroupDto(UUID itemId, Integer amount) {
}
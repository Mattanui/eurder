package com.eurder.eurder.service;

import java.util.UUID;

import static com.eurder.shared.Validation.requireValue;

public record ItemGroupRequest(UUID itemId, Integer amount) {

    public ItemGroupRequest {
        requireValue(itemId, "Item id");
    }
}

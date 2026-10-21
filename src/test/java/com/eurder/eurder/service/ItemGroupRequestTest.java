package com.eurder.eurder.service;

import com.eurder.shared.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemGroupRequestTest {

    @Test
    void createRequest_withoutItemId_isRejected() {
        assertThatThrownBy(() -> new ItemGroupRequest(null, 1))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Item id must be provided");
    }
}
package com.eurder.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    void admin_hasAccessToCreateItem() {
        assertThat(Role.ADMIN.hasAccessTo(Feature.CREATE_ITEM)).isTrue();
    }

    @Test
    void customer_hasNoAccessToCreateItem() {
        assertThat(Role.CUSTOMER.hasAccessTo(Feature.CREATE_ITEM)).isFalse();
    }
}
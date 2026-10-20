package com.eurder.customer.api.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateCustomerDtoTest {

    @Test
    void toString_doesNotRevealThePassword() {
        CreateCustomerDto dto = new CreateCustomerDto("Jimmy", "Hendrix", "jimmy.hendrix@dhemail.com",
                "aSecretPassword", null, null);

        assertThat(dto.toString())
                .doesNotContain("aSecretPassword")
                .contains("password=****");
    }
}
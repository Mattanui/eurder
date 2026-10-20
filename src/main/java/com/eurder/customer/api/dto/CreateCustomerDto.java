package com.eurder.customer.api.dto;

public record CreateCustomerDto(String firstName, String lastName, String email, String password,
                                AddressDto address, PhoneNumberDto phoneNumber) {

    @Override
    public String toString() {
        return "CreateCustomerDto[firstName=%s, lastName=%s, email=%s, password=****, address=%s, phoneNumber=%s]"
                .formatted(firstName, lastName, email, address, phoneNumber);
    }
}
package com.eurder.customer.api;

import com.eurder.customer.api.dto.AddressDto;
import com.eurder.customer.api.dto.CreateCustomerDto;
import com.eurder.customer.api.dto.CustomerDto;
import com.eurder.customer.api.dto.PhoneNumberDto;
import com.eurder.customer.domain.Address;
import com.eurder.customer.domain.Customer;
import com.eurder.customer.domain.PhoneNumber;
import com.eurder.shared.Email;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toCustomer(CreateCustomerDto dto) {
        return new Customer(
                dto.firstName(),
                dto.lastName(),
                new Email(dto.email()),
                toAddress(dto.address()),
                toPhoneNumber(dto.phoneNumber()));
    }

    public CustomerDto toDto(Customer customer) {
        return new CustomerDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail().value(),
                toAddressDto(customer.getAddress()),
                toPhoneNumberDto(customer.getPhoneNumber()));
    }

    // A missing address or phone number becomes null: the Customer itself rejects it (business rule)
    private Address toAddress(AddressDto dto) {
        if (dto == null) {
            return null;
        }
        return new Address(dto.street(), dto.houseNumber(), dto.postalCode(), dto.city(), dto.country());
    }

    private PhoneNumber toPhoneNumber(PhoneNumberDto dto) {
        if (dto == null) {
            return null;
        }
        return new PhoneNumber(dto.countryCode(), dto.number());
    }

    private AddressDto toAddressDto(Address address) {
        return new AddressDto(address.street(), address.houseNumber(), address.postalCode(),
                address.city(), address.country());
    }

    private PhoneNumberDto toPhoneNumberDto(PhoneNumber phoneNumber) {
        return new PhoneNumberDto(phoneNumber.countryCode(), phoneNumber.number());
    }
}
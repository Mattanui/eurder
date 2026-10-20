package com.eurder.customer.api;

import com.eurder.customer.api.dto.AddressDto;
import com.eurder.customer.api.dto.CreateCustomerDto;
import com.eurder.customer.api.dto.CustomerDto;
import com.eurder.customer.api.dto.PhoneNumberDto;
import com.eurder.customer.domain.Address;
import com.eurder.customer.domain.Customer;
import com.eurder.customer.domain.PhoneNumber;
import com.eurder.shared.Email;
import com.eurder.shared.InvalidInputException;
import org.junit.jupiter.api.Test;

import static com.eurder.customer.domain.CustomerTestData.aCustomer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerMapperTest {

    private static final AddressDto ADDRESS_DTO =
            new AddressDto("Rue de la guitare", "10A", "1000", "Brussels", "Belgium");
    private static final PhoneNumberDto PHONE_NUMBER_DTO = new PhoneNumberDto("+32", "485652311");

    private final CustomerMapper mapper = new CustomerMapper();

    @Test
    void toCustomer_mapsAllFields() {
        CreateCustomerDto dto = new CreateCustomerDto("Jimmy", "Hendrix", "jimmy.hendrix@dhemail.com",
                "aSecret", ADDRESS_DTO, PHONE_NUMBER_DTO);

        Customer customer = mapper.toCustomer(dto);

        assertThat(customer)
                .extracting(Customer::getFirstName, Customer::getLastName, Customer::getEmail,
                        Customer::getAddress, Customer::getPhoneNumber)
                .containsExactly("Jimmy", "Hendrix",
                        new Email("jimmy.hendrix@dhemail.com"),
                        new Address("Rue de la guitare", "10A", "1000", "Brussels", "Belgium"),
                        new PhoneNumber("+32", "485652311"));
    }

    @Test
    void toCustomer_withoutAddress_isRejected() {
        CreateCustomerDto dto = new CreateCustomerDto("Jimmy", "Hendrix", "jimmy.hendrix@dhemail.com",
                "aSecret", null, PHONE_NUMBER_DTO);

        assertThatThrownBy(() -> mapper.toCustomer(dto))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Address must be provided");
    }

    @Test
    void toCustomer_withoutPhoneNumber_isRejected() {
        CreateCustomerDto dto = new CreateCustomerDto("Jimmy", "Hendrix", "jimmy.hendrix@dhemail.com",
                "aSecret", ADDRESS_DTO, null);

        assertThatThrownBy(() -> mapper.toCustomer(dto))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Phone number must be provided");
    }

    @Test
    void toDto_mapsAllFields() {
        Customer customer = aCustomer();

        CustomerDto dto = mapper.toDto(customer);

        assertThat(dto).isEqualTo(new CustomerDto(customer.getId(), "Jimmy", "Hendrix",
                "jimmy.hendrix@dhemail.com", ADDRESS_DTO, PHONE_NUMBER_DTO));
    }
}
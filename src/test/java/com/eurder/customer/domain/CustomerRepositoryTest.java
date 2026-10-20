package com.eurder.customer.domain;

import org.junit.jupiter.api.Test;
import static com.eurder.customer.domain.CustomerTestData.aCustomer;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRepositoryTest {

    private final CustomerRepository repository = new CustomerRepository();

    @Test
    void save_thenFindById_returnsTheSavedCustomer() {
        Customer customer = aCustomer();

        repository.save(customer);

        assertThat(repository.findById(customer.getId())).containsSame(customer);
    }

    @Test
    void findById_withUnknownId_returnsEmpty() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }

}
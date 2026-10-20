package com.eurder.customer.domain;

import org.junit.jupiter.api.Test;

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

    private static Customer aCustomer() {
        return new Customer("Jimmy", "Hendrix",
                new Email("jimmy.hendrix@dhemail.com"),
                new Address("Rue de la guitare", "10A", "1000", "Brussels", "Belgium"),
                new PhoneNumber("+32", "485652311"));
    }
}
package com.eurder.customer.domain;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CustomerRepository {

    private final Map<UUID, Customer> customersById = new ConcurrentHashMap<>();

    public Customer save(Customer customer) {
        customersById.put(customer.getId(), customer);
        return customer;
    }

    public Optional<Customer> findById(UUID id) {
        return Optional.ofNullable(customersById.get(id));
    }
}
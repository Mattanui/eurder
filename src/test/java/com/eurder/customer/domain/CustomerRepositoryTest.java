package com.eurder.customer.domain;

import com.eurder.PersistenceTest;
import com.eurder.security.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static com.eurder.customer.domain.CustomerTestData.aCustomer;
import static com.eurder.customer.domain.CustomerTestData.aPersistedCustomer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@PersistenceTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void save_thenFindById_readsTheCustomerBackFromTheDatabase() {
        Customer customer = aPersistedCustomer(userRepository, repository);
        entityManager.flush();
        entityManager.clear();

        Customer found = repository.findById(customer.getId()).orElseThrow();

        assertThat(found).isNotSameAs(customer);
        assertThat(found).isEqualTo(customer);
        assertThat(found.getFirstName()).isEqualTo("Jimmy");
        assertThat(found.getLastName()).isEqualTo("Hendrix");
        assertThat(found.getEmail()).isEqualTo(customer.getEmail());
        assertThat(found.getAddress()).isEqualTo(customer.getAddress());
        assertThat(found.getPhoneNumber()).isEqualTo(customer.getPhoneNumber());
    }

    @Test
    void findById_withUnknownId_returnsEmpty() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    void save_withoutAMatchingUser_isRejectedByTheForeignKey() {
        Customer customerWithoutUser = aCustomer();

        assertThatThrownBy(() -> repository.saveAndFlush(customerWithoutUser))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
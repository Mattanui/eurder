package com.eurder.eurder.domain;

import com.eurder.PersistenceTest;
import com.eurder.customer.domain.Customer;
import com.eurder.customer.domain.CustomerRepository;
import com.eurder.item.domain.Item;
import com.eurder.item.domain.ItemRepository;
import com.eurder.item.domain.Price;
import com.eurder.security.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.eurder.customer.domain.CustomerTestData.aPersistedCustomer;
import static com.eurder.item.domain.ItemTestData.anItem;
import static org.assertj.core.api.Assertions.assertThat;

@PersistenceTest
class EurderRepositoryTest {

    private static final LocalDate ORDER_DATE = LocalDate.of(2026, 10, 21);

    @Autowired
    private EurderRepository repository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void save_thenFindById_readsTheEurderAndItsItemGroupsBackInOrder() {
        Customer customer = aPersistedCustomer(userRepository, customerRepository);
        Item guitar = itemRepository.save(anItem());
        Item strap = itemRepository.save(anItem());
        ItemGroup first = new ItemGroup(guitar.getId(), "Fender Stratocaster",
                new Price(new BigDecimal("699.95")), 2, ORDER_DATE.plusDays(1));
        ItemGroup second = new ItemGroup(strap.getId(), "Leather strap",
                new Price(new BigDecimal("49.99")), 3, ORDER_DATE.plusDays(7));
        Eurder eurder = repository.save(new Eurder(customer.getId(), ORDER_DATE, List.of(first, second)));
        entityManager.flush();
        entityManager.clear();

        Eurder found = repository.findById(eurder.getId()).orElseThrow();

        assertThat(found).isNotSameAs(eurder);
        assertThat(found.getCustomerId()).isEqualTo(customer.getId());
        assertThat(found.getOrderDate()).isEqualTo(ORDER_DATE);
        assertThat(found.getItemGroups()).containsExactly(first, second);
        assertThat(found.getTotalPrice()).isEqualTo(new Price(new BigDecimal("1549.87")));
    }

    @Test
    void findById_withUnknownId_returnsEmpty() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }
}
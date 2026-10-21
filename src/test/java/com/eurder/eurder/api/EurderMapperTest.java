package com.eurder.eurder.api;

import com.eurder.eurder.api.dto.CreateEurderDto;
import com.eurder.eurder.api.dto.CreateItemGroupDto;
import com.eurder.eurder.api.dto.EurderDto;
import com.eurder.eurder.api.dto.ItemGroupDto;
import com.eurder.eurder.domain.Eurder;
import com.eurder.eurder.domain.ItemGroup;
import com.eurder.eurder.service.ItemGroupRequest;
import com.eurder.item.domain.Price;
import com.eurder.shared.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EurderMapperTest {

    private final EurderMapper mapper = new EurderMapper();

    @Test
    void toItemGroupRequests_mapsEveryItemGroup() {
        UUID guitarId = UUID.randomUUID();
        UUID strapId = UUID.randomUUID();
        CreateEurderDto dto = new CreateEurderDto(List.of(
                new CreateItemGroupDto(guitarId, 2),
                new CreateItemGroupDto(strapId, 3)));

        assertThat(mapper.toItemGroupRequests(dto)).containsExactly(
                new ItemGroupRequest(guitarId, 2),
                new ItemGroupRequest(strapId, 3));
    }

    @Test
    void toItemGroupRequests_withoutItemGroups_returnsAnEmptyList() {
        assertThat(mapper.toItemGroupRequests(new CreateEurderDto(null))).isEmpty();
    }

    @Test
    void toItemGroupRequests_withANullItemGroup_isRejected() {
        CreateEurderDto dto = new CreateEurderDto(Arrays.asList((CreateItemGroupDto) null));

        assertThatThrownBy(() -> mapper.toItemGroupRequests(dto))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Item group must be provided");
    }

    @Test
    void toDto_mapsAllFieldsIncludingTotals() {
        UUID itemId = UUID.randomUUID();
        LocalDate orderDate = LocalDate.of(2026, 10, 21);
        Eurder eurder = new Eurder(UUID.randomUUID(), orderDate, List.of(
                new ItemGroup(itemId, "Fender Stratocaster", new Price(new BigDecimal("699.95")),
                        2, orderDate.plusDays(1))));

        EurderDto dto = mapper.toDto(eurder);

        assertThat(dto).isEqualTo(new EurderDto(eurder.getId(), eurder.getCustomerId(), orderDate,
                List.of(new ItemGroupDto(itemId, "Fender Stratocaster", new BigDecimal("699.95"),
                        2, LocalDate.of(2026, 10, 22), new BigDecimal("1399.90"))),
                new BigDecimal("1399.90")));
    }
}
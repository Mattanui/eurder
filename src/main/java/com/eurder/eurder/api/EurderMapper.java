package com.eurder.eurder.api;

import com.eurder.eurder.api.dto.CreateEurderDto;
import com.eurder.eurder.api.dto.CreateItemGroupDto;
import com.eurder.eurder.api.dto.EurderDto;
import com.eurder.eurder.api.dto.ItemGroupDto;
import com.eurder.eurder.domain.Eurder;
import com.eurder.eurder.domain.ItemGroup;
import com.eurder.eurder.service.ItemGroupRequest;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.eurder.shared.Validation.requireValue;

@Component
public class EurderMapper {

    public List<ItemGroupRequest> toItemGroupRequests(CreateEurderDto dto) {
        if (dto.itemGroups() == null) {
            return List.of(); // an empty order is rejected by the Eurder itself (business rule)
        }
        return dto.itemGroups().stream()
                .map(this::toItemGroupRequest)
                .toList();
    }

    public EurderDto toDto(Eurder eurder) {
        return new EurderDto(
                eurder.getId(),
                eurder.getCustomerId(),
                eurder.getOrderDate(),
                eurder.getItemGroups().stream().map(this::toItemGroupDto).toList(),
                eurder.getTotalPrice().amount());
    }

    private ItemGroupRequest toItemGroupRequest(CreateItemGroupDto dto) {
        requireValue(dto, "Item group");
        return new ItemGroupRequest(dto.itemId(), dto.amount());
    }

    private ItemGroupDto toItemGroupDto(ItemGroup itemGroup) {
        return new ItemGroupDto(
                itemGroup.itemId(),
                itemGroup.itemName(),
                itemGroup.unitPrice().amount(),
                itemGroup.amount(),
                itemGroup.shippingDate(),
                itemGroup.totalPrice().amount());
    }
}
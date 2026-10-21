package com.eurder.eurder.api.dto;

import java.util.List;

public record CreateEurderDto(List<CreateItemGroupDto> itemGroups) {
}
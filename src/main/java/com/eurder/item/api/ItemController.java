package com.eurder.item.api;

import com.eurder.infrastructure.OpenApiConfig;
import com.eurder.item.api.dto.CreateItemDto;
import com.eurder.item.api.dto.ItemDto;
import com.eurder.item.domain.Item;
import com.eurder.item.service.ItemService;
import com.eurder.security.Feature;
import com.eurder.security.SecurityService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final SecurityService securityService;

    public ItemController(ItemService itemService, ItemMapper itemMapper, SecurityService securityService) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
        this.securityService = securityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = OpenApiConfig.BASIC_AUTH)
    public ItemDto addItem(
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestBody CreateItemDto createItemDto) {
        securityService.validateAccess(authorization, Feature.CREATE_ITEM);
        Item item = itemMapper.toItem(createItemDto);
        return itemMapper.toDto(itemService.addItem(item));
    }
}
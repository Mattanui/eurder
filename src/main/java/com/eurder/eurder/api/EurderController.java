package com.eurder.eurder.api;

import com.eurder.eurder.api.dto.CreateEurderDto;
import com.eurder.eurder.api.dto.EurderDto;
import com.eurder.eurder.domain.Eurder;
import com.eurder.eurder.service.EurderService;
import com.eurder.infrastructure.OpenApiConfig;
import com.eurder.security.Feature;
import com.eurder.security.SecurityService;
import com.eurder.security.User;
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
@RequestMapping("/orders")
public class EurderController {

    private final EurderService eurderService;
    private final EurderMapper eurderMapper;
    private final SecurityService securityService;

    public EurderController(EurderService eurderService, EurderMapper eurderMapper,
                            SecurityService securityService) {
        this.eurderService = eurderService;
        this.eurderMapper = eurderMapper;
        this.securityService = securityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = OpenApiConfig.BASIC_AUTH)
    public EurderDto orderItems(
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestBody CreateEurderDto createEurderDto) {
        User customer = securityService.validateAccess(authorization, Feature.ORDER_ITEMS);
        Eurder eurder = eurderService.placeEurder(customer.getId(), eurderMapper.toItemGroupRequests(createEurderDto));
        return eurderMapper.toDto(eurder);
    }
}
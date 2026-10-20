package com.eurder.customer.api;

import com.eurder.customer.api.dto.CreateCustomerDto;
import com.eurder.customer.api.dto.CustomerDto;
import com.eurder.customer.domain.Customer;
import com.eurder.customer.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    public CustomerController(CustomerService customerService, CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto registerCustomer(@RequestBody CreateCustomerDto createCustomerDto) {
        Customer customer = customerMapper.toCustomer(createCustomerDto);
        Customer registeredCustomer = customerService.registerCustomer(customer, createCustomerDto.password());
        return customerMapper.toDto(registeredCustomer);
    }
}
package org.example.service;

import java.util.List;
import java.util.UUID;
import org.example.domain.entity.Customer;
import org.example.domain.enums.CustomerType;
import org.example.rest.dto_request.CustomerDtoRequest;
import org.example.rest.dto_response.CustomerDtoResponse;
import org.example.rest.dto_response.CustomerDtoResponseWithAdresses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    public CustomerDtoResponse save(CustomerDtoRequest customerDto);

    public Boolean existsCustomersByDocument(String document);

    public CustomerDtoResponseWithAdresses getCustomerById(UUID id);

    Page<CustomerDtoResponse> searchCustomers(String customerType, String name,
                                              Pageable pageable, String email,
                                              String phoneNumber, String document);
}

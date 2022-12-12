package org.example.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.domain.entity.Customer;
import org.example.domain.enums.CustomerType;
import org.example.rest.dto_request.CustomerDtoRequest;
import org.example.rest.dto_response.CustomerDtoResponse;
import org.example.rest.dto_response.CustomerDtoResponseWithAdresses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerDtoResponse save(CustomerDtoRequest customerDto);

    Boolean existsCustomersByDocument(String document);

    CustomerDtoResponseWithAdresses getCustomerById(UUID id);

    Page<CustomerDtoResponse> searchCustomers(String customerType, String name,
                                              Pageable pageable, String email,
                                              String phoneNumber, String document);

    void delete(UUID uuid);

    Optional<Customer> getCustomer(UUID uuid);

    CustomerDtoResponse update(UUID uuid, CustomerDtoRequest request);
}

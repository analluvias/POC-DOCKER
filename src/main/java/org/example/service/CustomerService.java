package org.example.service;

import java.time.LocalDate;
import java.util.UUID;
import org.example.domain.entity.Customer;
import org.example.rest.dto_request.CustomerDtoRequestV1;
import org.example.rest.dto_request.CustomerDtoRequestV2;
import org.example.rest.dto_request.UpdateCustomerDtoRequestV1;
import org.example.rest.dto_request.UpdateCustomerDtoRequestV2;
import org.example.rest.dto_response.CustomerDtoResponseV1;
import org.example.rest.dto_response.CustomerDtoResponseV2;
import org.example.rest.dto_response.CustomerDtoResponseWithAddressesV2;
import org.example.rest.dto_response.CustomerDtoResponseWithAddressesV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerService {

    CustomerDtoResponseWithAddressesV1 saveV1(CustomerDtoRequestV1 customerDto);

    Boolean existsCustomersByDocument(String document);

    CustomerDtoResponseWithAddressesV1 getCustomerByIdV1(UUID id);

    Page<CustomerDtoResponseV1> searchCustomers(String customerType, String name,
                                                Pageable pageable, String email,
                                                String phoneNumber, String document);

    Page<CustomerDtoResponseV2> searchCustomersV2(String customerType, String name
            , Pageable pageable, String email, String phoneNumber, String document, LocalDate value);

    void delete(UUID uuid);

    Customer getCustomer(UUID uuid);

    CustomerDtoResponseWithAddressesV1 update(UUID uuid, UpdateCustomerDtoRequestV1 request);

    CustomerDtoResponseWithAddressesV2 saveV2(CustomerDtoRequestV2 request);

    CustomerDtoResponseWithAddressesV2 getCustomerByIdV2(UUID id);

    @Transactional
    CustomerDtoResponseWithAddressesV2 updateV2(UUID uuid, UpdateCustomerDtoRequestV2 request);
}

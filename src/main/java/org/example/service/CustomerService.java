package org.example.service;

import org.example.rest.dto_request.CustomerDtoRequest;
import org.example.rest.dto_response.CustomerDtoResponse;

public interface CustomerService {

    public CustomerDtoResponse save(CustomerDtoRequest customerDto);

    public Boolean existsCustomersByDocument(String document);
}

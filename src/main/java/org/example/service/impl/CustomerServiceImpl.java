package org.example.service.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.domain.entity.Customer;
import org.example.domain.repository.CustomerRepository;
import org.example.rest.dto_request.CustomerDtoRequest;
import org.example.rest.dto_response.CustomerDtoResponse;
import org.example.rest.exception.exceptions.DocumentInUseException;
import org.example.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final ModelMapper modelMapper;

    private final CustomerRepository repository;

    private final AddressServiceImpl addressServiceImpl;

    @Transactional
    public Boolean existsCustomersByDocument(String document){
        Optional<Customer> customerByDocument = repository.findCustomerByDocument(document);

        return customerByDocument.isPresent();
    }

    @Transactional
    public CustomerDtoResponse save(CustomerDtoRequest customerDto){

        if (  Boolean.TRUE.equals(existsCustomersByDocument(customerDto.getDocument()))  ) {
            throw new DocumentInUseException();
        }

        Customer customer = modelMapper.map(customerDto, Customer.class);

        customer = repository.save(customer);

        addressServiceImpl.save(customerDto.getAddresses(), customer);

        return modelMapper.map(customer, CustomerDtoResponse.class);
    }

}

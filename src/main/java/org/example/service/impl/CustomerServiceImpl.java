package org.example.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.domain.entity.Customer;
import org.example.domain.enums.CustomerType;
import org.example.domain.repository.CustomerRepository;
import org.example.rest.dto_request.CustomerDtoRequest;
import org.example.rest.dto_response.AddressDtoResponse;
import org.example.rest.dto_response.CustomerDtoResponse;
import org.example.rest.dto_response.CustomerDtoResponseWithAdresses;
import org.example.rest.exception.exceptions.DocumentInUseException;
import org.example.rest.exception.exceptions.EmailInUseException;
import org.example.rest.exception.exceptions.EqualValueException;
import org.example.rest.exception.exceptions.InvalidCustomerTypeException;
import org.example.rest.exception.exceptions.ObjectNotFoundException;
import org.example.rest.exception.exceptions.PhoneNumberInUseException;
import org.example.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Boolean existsCustomersByEmail(String email){
        Optional<Customer> customerByEmail = repository.findByEmail(email);

        if (customerByEmail.isPresent())
            return true;
        return false;
    }

    @Transactional
    public Boolean existsCustomersByPhoneNumber(String phone){
        Optional<Customer> customerByPhoneNumber = repository.findByPhoneNumber(phone);

        return customerByPhoneNumber.isPresent();
    }

    @Transactional
    public CustomerDtoResponse save(CustomerDtoRequest customerDto){

        if (  Boolean.TRUE.equals(existsCustomersByDocument(customerDto.getDocument()))  ) {
            throw new DocumentInUseException();
        }

        if (Boolean.TRUE.equals(existsCustomersByEmail(customerDto.getEmail())) ){
            throw new EmailInUseException();
        }

        if (Boolean.TRUE.equals(existsCustomersByPhoneNumber(customerDto.getPhoneNumber()))){
            throw new PhoneNumberInUseException();
        }
        testCustomerType(customerDto);

        Customer customer = modelMapper.map(customerDto, Customer.class);

        customer = repository.save(customer);

        addressServiceImpl.save(customerDto.getAddresses(), customer);

        return modelMapper.map(customer, CustomerDtoResponse.class);
    }

    public CustomerDtoResponseWithAdresses getCustomerById(UUID id) {

        try {
            Optional<Customer> customer = repository.findById(id);

            List<AddressDtoResponse> addressesByCustomer = addressServiceImpl.getAddressesByCustomer(customer.get());


            CustomerDtoResponseWithAdresses response = modelMapper
                    .map(customer.get(), CustomerDtoResponseWithAdresses.class);

            response.setAddresses( addressesByCustomer );

            return response;

        } catch(java.util.NoSuchElementException e){

            throw new ObjectNotFoundException("Customer not found.");
        }

    }

    @Override
    public Page<CustomerDtoResponse> searchCustomers(String customerType, String name
            , Pageable pageable, String email, String phoneNumber, String document) {

        try {

            Customer customerToSearch = Customer.builder().build();

            if (customerType != null) {
                CustomerType cType = CustomerType.valueOf(customerType.toUpperCase());
                customerToSearch.setCustomerType(cType);
            }
            if (name != null){
                customerToSearch.setName(name);
            }
            if (email != null){
                customerToSearch.setEmail(email);
            }
            if (phoneNumber != null){
                customerToSearch.setPhoneNumber(phoneNumber);
            }
            if (document != null){
                customerToSearch.setDocument(document);
            }

            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withIgnoreCase("name", "email", "document")
                    .withStringMatcher(ExampleMatcher.StringMatcher.STARTING);

            Example<Customer> example = Example.of(customerToSearch, matcher);

            Page<Customer> customerPage = repository.findAll(example, pageable);

            return customerPage.map(customer -> modelMapper.map(customer, CustomerDtoResponse.class));

        }catch (java.lang.IllegalArgumentException e){

            throw new InvalidCustomerTypeException();

        }

    }

    @Transactional
    @Override
    public void delete(UUID uuid) {

        Optional<Customer> customer = getCustomer(uuid);

        if (customer.isEmpty()){
            throw new ObjectNotFoundException("the customer you tried to delete does not exist.");
        }

        addressServiceImpl.deleteAdressesByCustomer(customer.get());
        repository.delete(customer.get());
    }

    public Optional<Customer> getCustomer(UUID uuid) {
        Optional<Customer> customer = repository.findById(uuid);
        return customer;
    }

    @Transactional
    @Override
    public CustomerDtoResponse update(UUID uuid, CustomerDtoRequest request) {

        Optional<Customer> customer = getCustomer(uuid);

        if ( customer.isEmpty() )
            throw new ObjectNotFoundException("The customer you tried to update does not exist.");

        if (customer.get().getEmail().equals(request.getEmail()))
            throw new EqualValueException("Customer is already using this email. Try patch.");

        if (customer.get().getDocument().equals(request.getDocument()))
            throw new EqualValueException("Customer is already using this document. Try patch.");

        if (customer.get().getPhoneNumber().equals(request.getPhoneNumber()))
            throw new EqualValueException("Customer is already uding this phone number. Try patch.");

        if (  Boolean.TRUE.equals(existsCustomersByDocument(request.getDocument()))  )
            throw new DocumentInUseException();

        if (Boolean.TRUE.equals(existsCustomersByEmail(request.getEmail())) )
            throw new EmailInUseException();

        if (Boolean.TRUE.equals(existsCustomersByPhoneNumber(request.getPhoneNumber())))
            throw new PhoneNumberInUseException();

        testCustomerType(request);

        customer.get().setDocument( request.getDocument() );
        customer.get().setCustomerType( request.getCustomerType() );
        customer.get().setName( request.getName() );

        addressServiceImpl.deleteAdressesByCustomer(customer.get());
        addressServiceImpl.save(request.getAddresses(), customer.get());

        customer.get().setEmail(request.getEmail() );
        customer.get().setPhoneNumber( request.getPhoneNumber() );

        repository.save(customer.get());

        return modelMapper.map(customer.get(), CustomerDtoResponse.class);
    }

    private static void testCustomerType(CustomerDtoRequest request) {
        if ( !request.getCustomerType().toString().equals("FISICA")
                && !request.getCustomerType().toString().equals("JURIDICA") )
            throw new InvalidCustomerTypeException();
    }

}

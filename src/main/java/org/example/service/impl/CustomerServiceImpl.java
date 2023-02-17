package org.example.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.domain.entity.Customer;
import org.example.domain.enums.CustomerType;
import org.example.domain.repository.CustomerRepository;
import org.example.rest.dto_request.CustomerDtoRequestV1;
import org.example.rest.dto_request.CustomerDtoRequestV2;
import org.example.rest.dto_request.UpdateCustomerDtoRequestV1;
import org.example.rest.dto_request.UpdateCustomerDtoRequestV2;
import org.example.rest.dto_response.AddressDtoResponse;
import org.example.rest.dto_response.CustomerDtoResponseV1;
import org.example.rest.dto_response.CustomerDtoResponseV2;
import org.example.rest.dto_response.CustomerDtoResponseWithAddressesV1;
import org.example.rest.dto_response.CustomerDtoResponseWithAddressesV2;
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
import org.springframework.data.domain.PageImpl;
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

        return customerByEmail.isPresent();
    }

    @Transactional
    public Boolean existsCustomersByPhoneNumber(String phone){
        Optional<Customer> customerByPhoneNumber = repository.findByPhoneNumber(phone);

        return customerByPhoneNumber.isPresent();
    }

    @Transactional
    public CustomerDtoResponseWithAddressesV1 saveV1(CustomerDtoRequestV1 customerDto){

        if (  Boolean.TRUE.equals(existsCustomersByDocument(customerDto.getDocument()))  ) {
            throw new DocumentInUseException();
        }

        if (Boolean.TRUE.equals(existsCustomersByEmail(customerDto.getEmail())) ){
            throw new EmailInUseException();
        }

        if (Boolean.TRUE.equals(existsCustomersByPhoneNumber(customerDto.getPhoneNumber()))){
            throw new PhoneNumberInUseException();
        }

        testCustomerType(customerDto.getCustomerType());

        Customer customer = modelMapper.map(customerDto, Customer.class);

        customer.setDocument( reviewDocument(customer.getDocument()) );

        customer = repository.save(customer);

        CustomerDtoResponseWithAddressesV1 response = modelMapper.map(customer, CustomerDtoResponseWithAddressesV1.class);

        response.setAddresses(  addressServiceImpl.save(customerDto.getAddresses(), customer)  );

        return response;

    }


    public CustomerDtoResponseWithAddressesV1 getCustomerByIdV1(UUID id) {

        try {
            Optional<Customer> customer = repository.findById(id);

            List<AddressDtoResponse> addressesByCustomer = addressServiceImpl.getAddressesByCustomer(customer.get());


            CustomerDtoResponseWithAddressesV1 response = modelMapper
                    .map(customer.get(), CustomerDtoResponseWithAddressesV1.class);

            response.setAddresses( addressesByCustomer );

            return response;

        } catch(java.util.NoSuchElementException e){

            throw new ObjectNotFoundException("Customer not found.");
        }

    }

    @Override
    public Page<CustomerDtoResponseV1> searchCustomers(String customerType, String name
            , Pageable pageable, String email, String phoneNumber, String document) {

        try {

            Customer customerToSearch = buildCustomerToSearch(customerType, name, email, phoneNumber, document);

            if (customerToSearch.getCustomerType() == null && customerToSearch.getName() == null
                    && customerToSearch.getEmail() == null && customerToSearch.getPhoneNumber() == null
                    && customerToSearch.getDocument() == null){

                Page<CustomerDtoResponseV1> nullPage = new PageImpl<>(Collections.emptyList());
                return nullPage;

            }

            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withIgnoreCase("name", "email", "document")
                    .withStringMatcher(ExampleMatcher.StringMatcher.STARTING);

            Example<Customer> example = Example.of(customerToSearch, matcher);

            Page<Customer> customerPage = repository.findAll(example, pageable);

            return customerPage.map(customer -> modelMapper.map(customer, CustomerDtoResponseV1.class));

        }catch (java.lang.IllegalArgumentException e){

            throw new InvalidCustomerTypeException();

        }

    }


    @Override
    public Page<CustomerDtoResponseV2> searchCustomersV2(String customerType, String name
            , Pageable pageable, String email, String phoneNumber, String document, LocalDate value) {

        try {

            Customer customerToSearch = buildCustomerToSearch(customerType, name, email, phoneNumber, document);

            if (value != null){
                customerToSearch.setBirthDate(value);
            }

            if (customerToSearch.getCustomerType() == null && customerToSearch.getName() == null
                    && customerToSearch.getEmail() == null && customerToSearch.getPhoneNumber() == null
                    && customerToSearch.getDocument() == null && customerToSearch.getBirthDate() == null){

                Page<CustomerDtoResponseV2> nullPage = new PageImpl<>(Collections.emptyList());
                return nullPage;

            }

            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withIgnoreCase("name", "email", "document")
                    .withStringMatcher(ExampleMatcher.StringMatcher.STARTING);

            Example<Customer> example = Example.of(customerToSearch, matcher);

            Page<Customer> customerPage = repository.findAll(example, pageable);

            return customerPage.map(customer -> modelMapper.map(customer, CustomerDtoResponseV2.class));

        }catch (java.lang.IllegalArgumentException e){

            throw new InvalidCustomerTypeException();

        }

    }

    private static Customer buildCustomerToSearch(String customerType, String name, String email, String phoneNumber,
                                              String document) {

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

        return customerToSearch;

    }



    @Transactional
    @Override
    public void delete(UUID uuid) {

        Customer customer = getCustomer(uuid);

        addressServiceImpl.deleteAdressesByCustomer(customer);
        repository.delete(customer);
    }

    public Customer getCustomer(UUID uuid) {

        Optional<Customer> customer = repository.findById(uuid);

        if (customer.isEmpty()){
            throw new ObjectNotFoundException("the customer you tried to find does not exist.");
        }

        return customer.get();
    }

    @Transactional
    @Override
    public CustomerDtoResponseWithAddressesV1 update(UUID uuid, UpdateCustomerDtoRequestV1 request) {

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();

        Customer customer = validateUpdate(uuid, request);

        if (request.getAddresses() != null) {
            addressServiceImpl.deleteAdressesByCustomer(customer);
            addressServiceImpl.save(request.getAddresses(), customer);
        }

        addressDtoResponses = addressServiceImpl.getAddressesByCustomer(customer);

        Customer updatedCustomer = repository.save(customer);

        CustomerDtoResponseWithAddressesV1 response = modelMapper.map(updatedCustomer, CustomerDtoResponseWithAddressesV1.class);

        response.setAddresses(addressDtoResponses);

        return response;
    }


    private static void testCustomerType(String cType) {

        System.out.println(cType);

        if ( cType == null )
            throw new InvalidCustomerTypeException();
        else if ( !cType.equals("FISICA") && !cType.equals("JURIDICA"))
            throw new InvalidCustomerTypeException();

    }


    @Override
    @Transactional
    public CustomerDtoResponseWithAddressesV2 saveV2(CustomerDtoRequestV2 customerDtoV2) {

        testCustomerType(customerDtoV2.getCustomerType().toString());

        CustomerDtoRequestV1 customerDtoRequestV1 = modelMapper.map(customerDtoV2, CustomerDtoRequestV1.class);

        CustomerDtoResponseWithAddressesV1 savedV1 = saveV1(customerDtoRequestV1);

        Customer customer = getCustomer( savedV1.getId() );

        customer.setBirthDate( customerDtoV2.getBirthDate() );

        repository.save(customer);

        CustomerDtoResponseWithAddressesV2 v2ToReturn = modelMapper.map(customer, CustomerDtoResponseWithAddressesV2.class);

        v2ToReturn.setAddresses( savedV1.getAddresses() );

        return v2ToReturn;
    }


    @Override
    public CustomerDtoResponseWithAddressesV2 getCustomerByIdV2(UUID id) {
        try {
            Optional<Customer> customer = repository.findById(id);

            List<AddressDtoResponse> addressesByCustomer = addressServiceImpl.getAddressesByCustomer(customer.get());


            CustomerDtoResponseWithAddressesV2 response = modelMapper
                    .map(customer.get(), CustomerDtoResponseWithAddressesV2.class);

            response.setAddresses( addressesByCustomer );

            return response;

        } catch(java.util.NoSuchElementException e){

            throw new ObjectNotFoundException("Customer not found.");
        }

    }


    @Transactional
    @Override
    public CustomerDtoResponseWithAddressesV2 updateV2(UUID uuid, UpdateCustomerDtoRequestV2 request) {

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();

        Customer customer = validateUpdate(uuid, modelMapper.map(request, UpdateCustomerDtoRequestV1.class));

        if (request.getAddresses() != null) {
            addressServiceImpl.deleteAdressesByCustomer(customer);
            addressServiceImpl.save(request.getAddresses(), customer);
        }

        addressDtoResponses = addressServiceImpl.getAddressesByCustomer(customer);

        if (request.getBirthDate() != null)
            customer.setBirthDate(  request.getBirthDate()  );

        Customer updatedCustomer = repository.save(customer);

        CustomerDtoResponseWithAddressesV2 response = modelMapper.map(updatedCustomer, CustomerDtoResponseWithAddressesV2.class);

        response.setAddresses(addressDtoResponses);

        return response;
    }


    private Customer validateUpdate(UUID uuid, UpdateCustomerDtoRequestV1 request){

        Customer customer = getCustomer(uuid);

        if (  customer.getEmail().equals(request.getEmail())  )
            throw new EqualValueException("Customer is already using this email.");

        if (  customer.getDocument().equals(request.getDocument())  )
            throw new EqualValueException("Customer is already using this document.");

        if (  customer.getPhoneNumber().equals(request.getPhoneNumber())  )
            throw new EqualValueException("Customer is already uding this phone number.");

        if (  Boolean.TRUE.equals(existsCustomersByDocument(request.getDocument()))  )
            throw new DocumentInUseException();

        if (  Boolean.TRUE.equals(existsCustomersByEmail(request.getEmail())) )
            throw new EmailInUseException();

        if (  Boolean.TRUE.equals(existsCustomersByPhoneNumber(request.getPhoneNumber()))  )
            throw new PhoneNumberInUseException();

        if (  request.getDocument() != null  )
            customer.setDocument( reviewDocument(request.getDocument()) );

        if (  request.getName() != null  )
            customer.setName( request.getName() );

        if (request.getEmail() != null)
            customer.setEmail(request.getEmail() );

        if (request.getPhoneNumber() != null)
            customer.setPhoneNumber( request.getPhoneNumber() );

        return customer;
    }

    private String reviewDocument(String document) {

        document = document.replace("-", "");
        document = document.replace("/", "");
        document = document.replace(".", "");

        return document;

    }



}

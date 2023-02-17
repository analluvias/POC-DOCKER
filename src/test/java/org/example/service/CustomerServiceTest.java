package org.example.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.example.domain.entity.Customer;
import org.example.domain.enums.CustomerType;
import org.example.domain.repository.CustomerRepository;
import org.example.rest.dto_request.AddressDtoRequest;
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
import org.example.service.impl.AddressServiceImpl;
import org.example.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class CustomerServiceTest {

    @InjectMocks
    CustomerServiceImpl customerService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressServiceImpl addressServiceImpl;

    @Mock
    private CustomerDtoResponseWithAddressesV1 cDtoRWithAddresses;


    @Test
    @DisplayName("Should save a customer")
    void saveCustomerTest(){

        UUID uuid = UUID.randomUUID();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address2 = AddressDtoRequest.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addresses.add(address1);
        addresses.add(address2);

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();
        AddressDtoResponse response1 = AddressDtoResponse.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoResponse response2 = AddressDtoResponse.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoResponses.add(response1);
        addressDtoResponses.add(response2);


        String email = "ana@gmail.com";
        String phoneNumber = "83999999999";
        String document = "16091700081";

        CustomerDtoRequestV1 customerdto = CustomerDtoRequestV1.builder()
                .customerType(String.valueOf(CustomerType.FISICA))
                .name("Ana")
                .email(  email  )
                .phoneNumber(  phoneNumber  )
                .document(  document  )
                .addresses(  addresses  )
                .build();

        Customer customer = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(  document  )
                .build();

        Mockito.doReturn(Optional.empty())
                .when(customerRepository).findCustomerByDocument( document );

        Mockito.doReturn(Optional.empty())
                .when(customerRepository).findByEmail(  email  );

        Mockito.doReturn(Optional.empty())
                .when(customerRepository).findByPhoneNumber(  document  );

        when(modelMapper.map(customerdto, Customer.class))
                .thenReturn(  customer  );

        when(addressServiceImpl.save(addresses, customer))
                .thenReturn(  addressDtoResponses  );

        CustomerDtoResponseWithAddressesV1 finalResponse = CustomerDtoResponseWithAddressesV1.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document("16091700081")
                .build();

        when(modelMapper.map(any(Customer.class), eq(CustomerDtoResponseWithAddressesV1.class)))
                .thenReturn(finalResponse);

        when( customerRepository.save( customer ) )
                .thenReturn(  Customer.builder()
                                .id(uuid)
                                .customerType(CustomerType.FISICA)
                                .name("Ana")
                                .email(email)
                                .phoneNumber(phoneNumber)
                                .document("16091700081")
                                .build() );

        CustomerDtoResponseWithAddressesV1 mockResponse = mock(CustomerDtoResponseWithAddressesV1.class);

        doNothing().when(  mockResponse )
                .setAddresses(  addressDtoResponses  );

        //execucao
        CustomerDtoResponseWithAddressesV1 savedCustomer = customerService.saveV1(customerdto);

        //verificacao
        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(savedCustomer.getEmail()).isEqualTo(customerdto.getEmail());
        assertThat(savedCustomer.getDocument()).isEqualTo(customerdto.getDocument());
        assertThat(savedCustomer.getName()).isEqualTo(customerdto.getName());

        Mockito.verify(customerRepository, Mockito.times(1)).save(customer);
    }

    @Test
    @DisplayName("Should throw InvalidCustomerTypeException")
    void doNotsaveCustomerInvalidCustomerTypeExceptionNull(){

        UUID uuid = UUID.randomUUID();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address2 = AddressDtoRequest.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addresses.add(address1);
        addresses.add(address2);

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();
        AddressDtoResponse response1 = AddressDtoResponse.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoResponse response2 = AddressDtoResponse.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoResponses.add(response1);
        addressDtoResponses.add(response2);


        String email = "ana@gmail.com";
        String phoneNumber = "83999999999";
        String document = "16091700081";

        CustomerDtoRequestV1 customerdto = CustomerDtoRequestV1.builder()
                //.customerType(CustomerType.FISICA)
                .name("Ana")
                .email(  email  )
                .phoneNumber(  phoneNumber  )
                .document(  document  )
                .addresses(  addresses  )
                .build();

        Customer customer = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(  document  )
                .build();

        Mockito.doReturn(Optional.empty())
                .when(customerRepository).findCustomerByDocument( document );

        Mockito.doReturn(Optional.empty())
                .when(customerRepository).findByEmail(  email  );

        Mockito.doReturn(Optional.empty())
                .when(customerRepository).findByPhoneNumber(  document  );

        when(modelMapper.map(customerdto, Customer.class))
                .thenReturn(  customer  );

        when(addressServiceImpl.save(addresses, customer))
                .thenReturn(  addressDtoResponses  );

        CustomerDtoResponseWithAddressesV1 finalResponse = CustomerDtoResponseWithAddressesV1.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document("16091700081")
                .build();

        when(modelMapper.map(any(Customer.class), eq(CustomerDtoResponseWithAddressesV1.class)))
                .thenReturn(finalResponse);

        when( customerRepository.save( customer ) )
                .thenReturn(  Customer.builder()
                        .id(uuid)
                        .customerType(CustomerType.FISICA)
                        .name("Ana")
                        .email(email)
                        .phoneNumber(phoneNumber)
                        .document("16091700081")
                        .build() );

        CustomerDtoResponseWithAddressesV1 mockResponse = mock(CustomerDtoResponseWithAddressesV1.class);

        doNothing().when(  mockResponse )
                .setAddresses(  addressDtoResponses  );

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> customerService.saveV1(customerdto));

        //verificacao
        assertThat(exception)
                .isInstanceOf(InvalidCustomerTypeException.class)
                .hasMessage("Invalid customer type, should be FISICA or JURIDICA");
    }

    @Test
    @DisplayName("Should throw InvalidCustomerTypeException")
    void doNotsaveCustomerInvalidCustomerTypeException(){

        UUID uuid = UUID.randomUUID();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address2 = AddressDtoRequest.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addresses.add(address1);
        addresses.add(address2);

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();
        AddressDtoResponse response1 = AddressDtoResponse.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoResponse response2 = AddressDtoResponse.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoResponses.add(response1);
        addressDtoResponses.add(response2);


        String email = "ana@gmail.com";
        String phoneNumber = "83999999999";
        String document = "16091700081";

        CustomerDtoRequestV1 customerdto = CustomerDtoRequestV1.builder()
                .customerType("FISIC")
                .name("Ana")
                .email(  email  )
                .phoneNumber(  phoneNumber  )
                .document(  document  )
                .addresses(  addresses  )
                .build();

        Customer customer = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(  document  )
                .build();

        Mockito.doReturn(Optional.empty())
                .when(customerRepository).findCustomerByDocument( document );

        Mockito.doReturn(Optional.empty())
                .when(customerRepository).findByEmail(  email  );

        Mockito.doReturn(Optional.empty())
                .when(customerRepository).findByPhoneNumber(  document  );

        when(modelMapper.map(customerdto, Customer.class))
                .thenReturn(  customer  );

        when(addressServiceImpl.save(addresses, customer))
                .thenReturn(  addressDtoResponses  );

        CustomerDtoResponseWithAddressesV1 finalResponse = CustomerDtoResponseWithAddressesV1.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document("16091700081")
                .build();

        when(modelMapper.map(any(Customer.class), eq(CustomerDtoResponseWithAddressesV1.class)))
                .thenReturn(finalResponse);

        when( customerRepository.save( customer ) )
                .thenReturn(  Customer.builder()
                        .id(uuid)
                        .customerType(CustomerType.FISICA)
                        .name("Ana")
                        .email(email)
                        .phoneNumber(phoneNumber)
                        .document("16091700081")
                        .build() );

        CustomerDtoResponseWithAddressesV1 mockResponse = mock(CustomerDtoResponseWithAddressesV1.class);

        doNothing().when(  mockResponse )
                .setAddresses(  addressDtoResponses  );

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> customerService.saveV1(customerdto));

        //verificacao
        assertThat(exception)
                .isInstanceOf(InvalidCustomerTypeException.class)
                .hasMessage("Invalid customer type, should be FISICA or JURIDICA");
    }

    @Test
    @DisplayName("Should throw DocumentInUseException -> Document already in use")
    void doNotsaveCustomerTest(){

        UUID uuid = UUID.randomUUID();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address2 = AddressDtoRequest.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addresses.add(address1);
        addresses.add(address2);

        CustomerDtoRequestV1 customerdto = CustomerDtoRequestV1.builder()
                .customerType(String.valueOf(CustomerType.FISICA))
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .addresses(  addresses  )
                .build();

        Customer customer = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .build();

        Mockito.doReturn(Optional.of(customer))
                .when(customerRepository).findCustomerByDocument(customer.getDocument());

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> customerService.saveV1(customerdto));

        //verificacao
        assertThat(exception)
                .isInstanceOf(DocumentInUseException.class)
                .hasMessage("This document is already in use.");

    }

    @Test
    @DisplayName("Should throw DocumentInUseException -> Email already in use")
    void doNotsaveCustomerTestEmailInUSe(){

        UUID uuid = UUID.randomUUID();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address2 = AddressDtoRequest.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addresses.add(address1);
        addresses.add(address2);

        CustomerDtoRequestV1 customerdto = CustomerDtoRequestV1.builder()
                .customerType(String.valueOf(CustomerType.FISICA))
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .addresses(  addresses  )
                .build();

        Customer customer = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .build();

        Mockito.doReturn(Optional.of(customer))
                .when(customerRepository).findByEmail(customer.getEmail());

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> customerService.saveV1(customerdto));

        //verificacao
        assertThat(exception)
                .isInstanceOf(EmailInUseException.class)
                .hasMessage("This email is already in use. choose another one.");
    }

    @Test
    @DisplayName("Should throw DocumentInUseException -> PhoneNumber already in use")
    void doNotsaveCustomerTestPhoneNumberInUSe(){

        UUID uuid = UUID.randomUUID();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address2 = AddressDtoRequest.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addresses.add(address1);
        addresses.add(address2);

        CustomerDtoRequestV1 customerdto = CustomerDtoRequestV1.builder()
                .customerType(String.valueOf(CustomerType.FISICA))
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .addresses(  addresses  )
                .build();

        Customer customer = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .build();

        Mockito.doReturn(Optional.of(customer))
                .when(customerRepository).findByPhoneNumber(customer.getPhoneNumber());

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> customerService.saveV1(customerdto));

        //verificacao
        assertThat(exception)
                .isInstanceOf(PhoneNumberInUseException.class)
                .hasMessage("This phone number is already in use. choose another one.");
    }

    @Test
    @DisplayName("should find a customer by its id")
    void shouldFindACustomerByItsId(){

        UUID uuid = UUID.randomUUID();

        Optional<Customer> customer = Optional.ofNullable(Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("16091700081")
                .build());

        when( customerRepository.findById( uuid ) )
                .thenReturn( customer );

        List<AddressDtoResponse> addressesByCustomer = new ArrayList<>();

        AddressDtoResponse addressDtoResponse = AddressDtoResponse.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addressesByCustomer.add(addressDtoResponse);

        when(addressServiceImpl.getAddressesByCustomer(customer.get())).thenReturn(addressesByCustomer );



        when( modelMapper.map(customer.get(), CustomerDtoResponseWithAddressesV1.class) ).thenReturn(
                CustomerDtoResponseWithAddressesV1.builder()
                        .id(uuid)
                        .customerType(CustomerType.FISICA)
                        .name("Ana")
                        .email("ana@gmail.com")
                        .phoneNumber("83999999999")
                        .document("160.917.000-81")
                        .addresses( addressesByCustomer )
                        .build()
        );

        //execucao
        CustomerDtoResponseWithAddressesV1 customerById = customerService.getCustomerByIdV1(uuid);

        //verificaçao
        assertThat(customerById.getId()).isEqualTo(uuid);

    }

    @Test
    @DisplayName("should throw error when search a not existent customer by its id")
    void shouldNotFindACustomerByItsId(){

        UUID uuid = UUID.randomUUID();

        when( customerRepository.findById( uuid ) )
                .thenReturn(Optional.empty());

        //execucao
        Throwable exception = Assertions.catchThrowable(()->customerService.getCustomerByIdV1(uuid)) ;

        //verificacao
        assertThat(exception)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Customer not found.");

    }


    @Test
    @DisplayName("should search a customer by properties")
    void shouldGetCustomerBySearch(){

        UUID uuid = UUID.randomUUID();

        Customer customerSearched = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("16091700081")
                .build();

        Page<Customer> customerPage = new PageImpl<>(List.of(customerSearched));

        when(  customerRepository.findAll(  (  Example<Customer>) any(), (Pageable) any())  )
                .thenReturn(customerPage);

        Page<CustomerDtoResponseV1> page = customerService.searchCustomers("FISICA", "Ana", Pageable.ofSize(20),
                "ana@gmail.com", "83999999999", "16091700081");

        assertThat(page.getTotalElements()).isEqualTo(1);
    }


    @Test
    @DisplayName("should search a customer by properties")
    void shouldReturnNullWhenTheRequestIsEmptyV1(){

        UUID uuid = UUID.randomUUID();

        Customer customerSearched = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("16091700081")
                .build();

        Page<Customer> customerPage = new PageImpl<>(List.of(customerSearched));

        when(  customerRepository.findAll(  (  Example<Customer>) any(), (Pageable) any())  )
                .thenReturn(customerPage);

        Page<CustomerDtoResponseV1> page = customerService.searchCustomers(null, null, null,
                null, null, null);

        assertThat(page.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("should search a customer by properties with null customer type")
    void shouldGetCustomerBySearchWithNullCType(){

        UUID uuid = UUID.randomUUID();

        Customer customerSearched = Customer.builder()
                //.customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("16091700081")
                .build();

        Page<Customer> customerPage = new PageImpl<>(List.of(customerSearched));

        when(  customerRepository.findAll(  (  Example<Customer>) any(), (Pageable) any())  )
                .thenReturn(customerPage);

        Page<CustomerDtoResponseV1> page = customerService.searchCustomers(null, "Ana", Pageable.ofSize(20),
                "ana@gmail.com", "83999999999", "16091700081");

        assertThat(page.getTotalElements()).isEqualTo(1);
    }


    @Test
    @DisplayName("should search a customer by properties with null name")
    void shouldGetCustomerBySearchWithNullName(){

        UUID uuid = UUID.randomUUID();

        Customer customerSearched = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                //.name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("16091700081")
                .build();

        Page<Customer> customerPage = new PageImpl<>(List.of(customerSearched));

        when(  customerRepository.findAll(  (  Example<Customer>) any(), (Pageable) any())  )
                .thenReturn(customerPage);

        Page<CustomerDtoResponseV1> page = customerService.searchCustomers("FISICA", null, Pageable.ofSize(20),
                "ana@gmail.com", "83999999999", "16091700081");

        assertThat(page.getTotalElements()).isEqualTo(1);
    }


    @Test
    @DisplayName("should search a customer by properties with null email")
    void shouldGetCustomerBySearchWithNullEmail(){

        UUID uuid = UUID.randomUUID();

        Customer customerSearched = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                //.email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("16091700081")
                .build();

        Page<Customer> customerPage = new PageImpl<>(List.of(customerSearched));

        when(  customerRepository.findAll(  (  Example<Customer>) any(), (Pageable) any())  )
                .thenReturn(customerPage);

        Page<CustomerDtoResponseV1> page = customerService.searchCustomers("FISICA", "Ana", Pageable.ofSize(20),
                null, "83999999999", "16091700081");

        assertThat(page.getTotalElements()).isEqualTo(1);
    }


    @Test
    @DisplayName("should search a customer by properties with null phone number and document")
    void shouldGetCustomerBySearchWithNullPhoneNumberAndDocument(){

        UUID uuid = UUID.randomUUID();

        Customer customerSearched = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana@gmail.com")
                //.phoneNumber("83999999999")
                //.document("16091700081")
                .build();

        Page<Customer> customerPage = new PageImpl<>(List.of(customerSearched));

        when(  customerRepository.findAll(  (  Example<Customer>) any(), (Pageable) any())  )
                .thenReturn(customerPage);

        Page<CustomerDtoResponseV1> page = customerService.searchCustomers("FISICA", "Ana", Pageable.ofSize(20),
                "ana@gmail.com", null, null);

        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("should Throw Error When Customer Type Not Valid")
    void shouldThrowErrorWhenCustomerTypeNotValid(){

        UUID uuid = UUID.randomUUID();

        Customer customerSearched = Customer.builder()
                .id(uuid)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("16091700081")
                .build();

        Page<Customer> customerPage = new PageImpl<>(List.of(customerSearched));

        when(  customerRepository.findAll(  (  Example<Customer>) any(), (Pageable) any())  )
                .thenReturn(customerPage);

        Throwable exception = Assertions.catchThrowable( ()-> customerService.searchCustomers("FIS", "Ana", Pageable.ofSize(20),
                "ana@gmail.com", "83999999999", "16091700081") );


        //verificacao
        assertThat(exception)
                .isInstanceOf(InvalidCustomerTypeException.class)
                .hasMessage("Invalid customer type, should be FISICA or JURIDICA");

    }

    @Test
    @DisplayName("should delete a customer and their addresses")
    void shouldDeleteAcustomer(){

        UUID uuid = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .name("ana")
                .document("16091700081")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .build();

        when(customerRepository.findById(uuid))
                .thenReturn(Optional.ofNullable(customer));

        doNothing().when(addressServiceImpl).deleteAdressesByCustomer(customer);

        doNothing().when(customerRepository).delete(customer);

        //execução
        org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> customerService.delete(uuid));

        //verificação
        Mockito.verify( customerRepository, Mockito.times(1) ).delete(customer);

    }

    @Test
    @DisplayName("should throw error when try to delete a nonexistent customer")
    void shouldThrowErrorWhenDeleteANonExistentCustomer(){

        UUID uuid = UUID.randomUUID();

        when(customerRepository.findById(uuid))
                .thenReturn(Optional.empty());


        //execução
        org.junit.jupiter.api.Assertions.assertThrows
                (ObjectNotFoundException.class ,() -> customerService.delete(uuid));

        //verificação
        Mockito.verify( customerRepository, Mockito.never() ).delete(any());


    }

    @Test
    @DisplayName("should update a customer")
    void shouldUpdateACustomer(){

        UUID uuid = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana0@gmail.com")
                .phoneNumber("83987878787")
                .document("05140557070")
                .build();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);

        String document = "16091700081";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        UpdateCustomerDtoRequestV1 customerdto = UpdateCustomerDtoRequestV1.builder()
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .addresses(  addresses  )
                .build();


        BDDMockito.when(customerRepository.findById(uuid)).thenReturn(Optional.ofNullable(customer));


        when(customerRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        when(customerRepository.findCustomerByDocument(document))
                .thenReturn(Optional.empty());


        when(customerRepository.findByPhoneNumber(phoneNumber))
                .thenReturn(Optional.empty());


        doNothing().when(addressServiceImpl).deleteAdressesByCustomer(customer);

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();
        AddressDtoResponse response1 = AddressDtoResponse.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoResponse response2 = AddressDtoResponse.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoResponses.add(response1);
        addressDtoResponses.add(response2);

        when(addressServiceImpl.save(addresses, customer))
                .thenReturn(  addressDtoResponses  );

        Customer customerToSave = Customer.builder()
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .build();

        Customer customerSaved = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .build();

        when(customerRepository.save(customerToSave)).thenReturn(customerSaved);


        when( modelMapper.map(any(), eq(CustomerDtoResponseWithAddressesV1.class)) ).thenReturn(
                CustomerDtoResponseWithAddressesV1.builder()
                        .id(uuid)
                        .customerType(CustomerType.FISICA)
                        .name("Ana")
                        .email("ana@gmail.com")
                        .phoneNumber("83999999999")
                        .document("160.917.000-81")
                        .addresses(  addressDtoResponses  )
                        .build()
        );

        //execução
        customerService.update(uuid, customerdto);

        //verificação
        assert customer != null;
        Mockito.verify(customerRepository, Mockito.times(1)).save(customer);
    }


    @Test
    @DisplayName("should update a customer without addresses")
    void shouldUpdateACustomerWithoutAddresses(){

        UUID uuid = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana0@gmail.com")
                .phoneNumber("83987878787")
                .document("05140557070")
                .build();

        String document = "16091700081";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        UpdateCustomerDtoRequestV1 customerdto = UpdateCustomerDtoRequestV1.builder()
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                //.addresses(  addresses  )
                .build();


        BDDMockito.when(customerRepository.findById(uuid)).thenReturn(Optional.ofNullable(customer));


        when(customerRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        when(customerRepository.findCustomerByDocument(document))
                .thenReturn(Optional.empty());


        when(customerRepository.findByPhoneNumber(phoneNumber))
                .thenReturn(Optional.empty());


        doNothing().when(addressServiceImpl).deleteAdressesByCustomer(customer);

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();
        AddressDtoResponse response1 = AddressDtoResponse.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoResponse response2 = AddressDtoResponse.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoResponses.add(response1);
        addressDtoResponses.add(response2);

        Customer customerToSave = Customer.builder()
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .build();

        Customer customerSaved = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .build();

        when(customerRepository.save(customerToSave)).thenReturn(customerSaved);


        when( modelMapper.map(any(), eq(CustomerDtoResponseWithAddressesV1.class)) ).thenReturn(
                CustomerDtoResponseWithAddressesV1.builder()
                        .id(uuid)
                        .customerType(CustomerType.FISICA)
                        .name("Ana")
                        .email("ana@gmail.com")
                        .phoneNumber("83999999999")
                        .document("160.917.000-81")
                        .addresses(  addressDtoResponses  )
                        .build()
        );

        //execução
        customerService.update(uuid, customerdto);

        //verificação
        assert customer != null;
        Mockito.verify(customerRepository, Mockito.times(1)).save(customer);
    }

    @Test
    @DisplayName("should throw exception when try to update nonexistent customer")
    void shouldThrowExceptionWhenCustomerRequestDoesNotExist(){

        UUID uuid = UUID.randomUUID();

        String document = "16091700081";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);

        UpdateCustomerDtoRequestV1 customerdto = UpdateCustomerDtoRequestV1.builder()
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .addresses(  addresses  )
                .build();

        BDDMockito.when(customerRepository.findById(uuid)).thenReturn(Optional.empty());

        //execução
        org.junit.jupiter.api.Assertions.assertThrows
                (ObjectNotFoundException.class ,() -> customerService.update(uuid, customerdto));

        //verificação
        Mockito.verify(customerRepository, Mockito.never()).save(any());

    }

    @Test
    @DisplayName("should throw exception when email already in use by the customer")
    void shouldThrowExceptionWhenEmailAlreadyInUseByTheCustomer(){

        UUID uuid = UUID.randomUUID();

        String document = "16091700081";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);
        UpdateCustomerDtoRequestV1 customerdto = UpdateCustomerDtoRequestV1.builder()
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .addresses(  addresses  )
                .build();

        //customerById
        Customer customerById = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .build();

        BDDMockito.when(customerRepository.findById(uuid)).thenReturn(Optional.ofNullable(customerById));


        //execução
        org.junit.jupiter.api.Assertions.assertThrows
                (EqualValueException.class ,() -> customerService.update(uuid, customerdto));

        //verificação
        Mockito.verify(customerRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("should throw exception when document already in use by the customer")
    void shouldThrowExceptionWhenDocumentAlreadyInUseByTheCustomer(){

        UUID uuid = UUID.randomUUID();

        String document = "16091700081";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);
        UpdateCustomerDtoRequestV1 customerdto = UpdateCustomerDtoRequestV1.builder()
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .addresses(  addresses  )
                .build();

        //customerById
        Customer customerById = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email("emailteste@gmail.com")
                .phoneNumber(phoneNumber)
                .document(document)
                .build();

        BDDMockito.when(customerRepository.findById(uuid)).thenReturn(Optional.ofNullable(customerById));


        //execução
        org.junit.jupiter.api.Assertions.assertThrows
                (EqualValueException.class ,() -> customerService.update(uuid, customerdto));

        //verificação
        Mockito.verify(customerRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("should throw exception when phone number already in use by the customer")
    void shouldThrowExceptionWhenPhoneAlreadyInUseByTheCustomer(){

        UUID uuid = UUID.randomUUID();

        String document = "16091700081";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);
        UpdateCustomerDtoRequestV1 customerdto = UpdateCustomerDtoRequestV1.builder()
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .addresses(  addresses  )
                .build();

        //customerById
        Customer customerById = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email("emailteste@gmail.com")
                .phoneNumber(phoneNumber)
                .document("62942154047")
                .build();

        BDDMockito.when(customerRepository.findById(uuid)).thenReturn(Optional.ofNullable(customerById));


        //execução
        org.junit.jupiter.api.Assertions.assertThrows
                (EqualValueException.class ,() -> customerService.update(uuid, customerdto));

        //verificação
        Mockito.verify(customerRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("should throw exception when email already in use by another customer")
    void shouldThrowExceptionWhenEmailAlreadyInUseByAnotherCustomer(){

        UUID uuid = UUID.randomUUID();

        String document = "16091700081";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);
        UpdateCustomerDtoRequestV1 customerdto = UpdateCustomerDtoRequestV1.builder()
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .addresses(  addresses  )
                .build();

        //customerById
        Customer customerById = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email("emailteste@gmail.com")
                .phoneNumber("83999999998")
                .document("62942154047")
                .build();

        BDDMockito.when(customerRepository.findById(uuid)).thenReturn(Optional.ofNullable(customerById));

        Customer customerWithSameEmail = Customer.builder()
                .id(UUID.randomUUID())
                .customerType(CustomerType.FISICA)
                .name("joao")
                .email(email)
                .phoneNumber("83955554444")
                .document("24487627087")
                .build();

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customerWithSameEmail));


        //execução
        org.junit.jupiter.api.Assertions.assertThrows
                (EmailInUseException.class ,() -> customerService.update(uuid, customerdto));

        //verificação
        Mockito.verify(customerRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("should throw exception when document already in use by another customer")
    void shouldThrowExceptionWhenDocumentAlreadyInUseByAnotherCustomer(){

        UUID uuid = UUID.randomUUID();

        String document = "16091700081";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);
        UpdateCustomerDtoRequestV1 customerdto = UpdateCustomerDtoRequestV1.builder()
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .addresses(  addresses  )
                .build();

        //customerById
        Customer customerById = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email("emailteste@gmail.com")
                .phoneNumber("83999999998")
                .document("62942154047")
                .build();

        BDDMockito.when(customerRepository.findById(uuid)).thenReturn(Optional.ofNullable(customerById));

        Customer customerWithSameDocument = Customer.builder()
                .id(UUID.randomUUID())
                .customerType(CustomerType.FISICA)
                .name("joao")
                .phoneNumber("83955554444")
                .email("emaildiferente@gmail.com")
                .document(document)
                .build();


        when(customerRepository.findCustomerByDocument(document)).thenReturn(Optional.of(customerWithSameDocument));

        //execução
        org.junit.jupiter.api.Assertions.assertThrows
                (DocumentInUseException.class ,() -> customerService.update(uuid, customerdto));

        //verificação
        Mockito.verify(customerRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("should throw exception when phone number already in use by another customer")
    void shouldThrowExceptionWhenPhoneNumberAlreadyInUseByAnotherCustomer(){

        UUID uuid = UUID.randomUUID();

        String document = "16091700081";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);
        UpdateCustomerDtoRequestV1 customerdto = UpdateCustomerDtoRequestV1.builder()
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .addresses(  addresses  )
                .build();

        //customerById
        Customer customerById = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email("emailteste@gmail.com")
                .phoneNumber("83999999998")
                .document("629.421.540-47")
                .build();

        BDDMockito.when(customerRepository.findById(uuid)).thenReturn(Optional.ofNullable(customerById));

        Customer customerWithSamePhone = Customer.builder()
                .id(UUID.randomUUID())
                .customerType(CustomerType.FISICA)
                .name("joao")
                .phoneNumber(phoneNumber)
                .email("emaildiferente@gmail.com")
                .document("24487627087")
                .build();


        when(customerRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(customerWithSamePhone));

        //execução
        org.junit.jupiter.api.Assertions.assertThrows
                (PhoneNumberInUseException.class ,() -> customerService.update(uuid, customerdto));

        //verificação
        Mockito.verify(customerRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("should save customer v2")
    void shouldSaveCustomerV2() {


        String email = "ana@gmail.com";
        String phoneNumber = "83999999999";
        String document = "16091700081";
        UUID uuid = UUID.randomUUID();


        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address2 = AddressDtoRequest.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addresses.add(address1);
        addresses.add(address2);


        CustomerDtoRequestV2 customerdtoV2 = CustomerDtoRequestV2.builder()
                .customerType(String.valueOf(CustomerType.FISICA))
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .name("Ana")
                .email(  email  )
                .phoneNumber(  phoneNumber  )
                .document(  document  )
                .addresses(  addresses  )
                .build();


        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();
        AddressDtoResponse response1 = AddressDtoResponse.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoResponse response2 = AddressDtoResponse.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoResponses.add(response1);
        addressDtoResponses.add(response2);


        CustomerDtoRequestV1 customerdtoV1 = CustomerDtoRequestV1.builder()
                .customerType(String.valueOf(CustomerType.FISICA))
                .name("Ana")
                .email(  email  )
                .phoneNumber(  phoneNumber  )
                .document(  document  )
                .addresses(  addresses  )
                .build();

        Customer customer = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(  document  )
                .build();

        when(modelMapper.map(customerdtoV2, CustomerDtoRequestV1.class))
                .thenReturn(  customerdtoV1  );

        Mockito.doReturn(Optional.empty())
                .when(customerRepository).findCustomerByDocument( document );

        Mockito.doReturn(Optional.empty())
                .when(customerRepository).findByEmail(  email  );

        Mockito.doReturn(Optional.empty())
                .when(customerRepository).findByPhoneNumber(  document  );

        when(modelMapper.map(customerdtoV1, Customer.class))
                .thenReturn(  customer  );

        when(addressServiceImpl.save(addresses, customer))
                .thenReturn(  addressDtoResponses  );

        CustomerDtoResponseWithAddressesV1 finalResponseV1 = CustomerDtoResponseWithAddressesV1.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document("16091700081")
                .build();

        when(modelMapper.map(any(Customer.class), eq(CustomerDtoResponseWithAddressesV1.class)))
                .thenReturn(finalResponseV1);

        when( customerRepository.save( customer ) )
                .thenReturn(  Customer.builder()
                        .id(uuid)
                        .customerType(CustomerType.FISICA)
                        .name("Ana")
                        .email(email)
                        .phoneNumber(phoneNumber)
                        .document("16091700081")
                        .build() );

        when(customerRepository.findById(uuid)).thenReturn(Optional.of(customer));


        CustomerDtoResponseWithAddressesV2 v2ToReturn = CustomerDtoResponseWithAddressesV2.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .name("Ana")
                .email(  email  )
                .phoneNumber(  phoneNumber  )
                .document(  document  )
                .addresses(  addressDtoResponses  )
                .build();
        when(modelMapper.map(any(Customer.class), eq(CustomerDtoResponseWithAddressesV2.class)))
                .thenReturn(v2ToReturn);


        CustomerDtoResponseWithAddressesV1 mockResponse = mock(CustomerDtoResponseWithAddressesV1.class);

        doNothing().when(  mockResponse )
                .setAddresses(  addressDtoResponses  );

        //execucao
        CustomerDtoResponseWithAddressesV2 savedCustomer = customerService.saveV2(customerdtoV2);

        //verificacao
        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(savedCustomer.getEmail()).isEqualTo(customerdtoV1.getEmail());
        assertThat(savedCustomer.getDocument()).isEqualTo(customerdtoV1.getDocument());
        assertThat(savedCustomer.getName()).isEqualTo(customerdtoV1.getName());

        Mockito.verify(customerRepository, Mockito.times(2)).save(customer);

    }


    @Test
    @DisplayName("should find a customer by its id - V2")
    void shouldFindACustomerByItsIdV2(){

        UUID uuid = UUID.randomUUID();

        Optional<Customer> customer = Optional.ofNullable(Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("16091700081")
                .build());

        when( customerRepository.findById( uuid ) )
                .thenReturn( customer );

        List<AddressDtoResponse> addressesByCustomer = new ArrayList<>();

        AddressDtoResponse addressDtoResponse = AddressDtoResponse.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addressesByCustomer.add(addressDtoResponse);

        when(addressServiceImpl.getAddressesByCustomer(customer.get())).thenReturn(addressesByCustomer );



        when( modelMapper.map(customer.get(), CustomerDtoResponseWithAddressesV2.class) ).thenReturn(
                CustomerDtoResponseWithAddressesV2.builder()
                        .id(uuid)
                        .customerType(CustomerType.FISICA)
                        .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                        .name("Ana")
                        .email("ana@gmail.com")
                        .phoneNumber("83999999999")
                        .document("160.917.000-81")
                        .addresses( addressesByCustomer )
                        .build()
        );

        //execucao
        CustomerDtoResponseWithAddressesV2 customerById = customerService.getCustomerByIdV2(uuid);

        //verificaçao
        assertThat(customerById.getId()).isEqualTo(uuid);
        Mockito.verify(customerRepository, Mockito.times(1)).findById(uuid);
    }

    @Test
    @DisplayName("should throw error when search a not existent customer by its id - V2")
    void shouldNotFindACustomerByItsIdV2(){

        UUID uuid = UUID.randomUUID();

        when( customerRepository.findById( uuid ) )
                .thenReturn(Optional.empty());

        //execucao
        Throwable exception = Assertions.catchThrowable(()->customerService.getCustomerByIdV2(uuid)) ;

        //verificacao
        assertThat(exception)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Customer not found.");

    }


    @Test
    @DisplayName("should search a customer by properties - v2")
    void shouldGetCustomerBySearchV2(){

        UUID uuid = UUID.randomUUID();

        Customer customerSearched = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("16091700081")
                .build();

        Page<Customer> customerPage = new PageImpl<>(List.of(customerSearched));

        when(  customerRepository.findAll(  (  Example<Customer>) any(), (Pageable) any())  )
                .thenReturn(customerPage);

        Page<CustomerDtoResponseV2> page = customerService.searchCustomersV2("FISICA", "Ana", Pageable.ofSize(20),
                "ana@gmail.com", "83999999999", "16091700081", LocalDate.of(2020, Month.JANUARY, 8));

        assertThat(page.getTotalElements()).isEqualTo(1);
    }


    @Test
    @DisplayName("should return null page when null request - v2")
    void shouldReturnNullPageWhenNullRequestV2(){

        UUID uuid = UUID.randomUUID();

        Customer customerSearched = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("16091700081")
                .build();

        Page<Customer> customerPage = new PageImpl<>(List.of(customerSearched));

        when(  customerRepository.findAll(  (  Example<Customer>) any(), (Pageable) any())  )
                .thenReturn(customerPage);


        Page<CustomerDtoResponseV2> page = customerService.searchCustomersV2(null, null, null,
                null, null, null, null);

        assertThat(page.getTotalElements()).isEqualTo(0);
    }


    @Test
    @DisplayName("should search a customer by properties - v2 - without birthDate")
    void shouldGetCustomerBySearchV2WithoutBirthDate(){

        UUID uuid = UUID.randomUUID();

        Customer customerSearched = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("16091700081")
                .build();

        Page<Customer> customerPage = new PageImpl<>(List.of(customerSearched));

        when(  customerRepository.findAll(  (  Example<Customer>) any(), (Pageable) any())  )
                .thenReturn(customerPage);

        Page<CustomerDtoResponseV2> page = customerService.searchCustomersV2("FISICA", "Ana", Pageable.ofSize(20),
                "ana@gmail.com", "83999999999", "16091700081", null);

        assertThat(page.getTotalElements()).isEqualTo(1);
    }


    @Test
    @DisplayName("should Throw Error When Customer Type Not Valid - v2")
    void shouldThrowErrorWhenCustomerTypeNotValidV2(){

        UUID uuid = UUID.randomUUID();

        Customer customerSearched = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("16091700081")
                .build();


        Page<Customer> customerPage = new PageImpl<>(List.of(customerSearched));

        when(  customerRepository.findAll(  (  Example<Customer>) any(), (Pageable) any())  )
                .thenReturn(customerPage);

        Throwable exception = Assertions.catchThrowable( ()-> customerService.searchCustomersV2("FIS", "Ana", Pageable.ofSize(20),
                "ana@gmail.com", "83999999999", "16091700081", null) );


        //verificacao
        assertThat(exception)
                .isInstanceOf(InvalidCustomerTypeException.class)
                .hasMessage("Invalid customer type, should be FISICA or JURIDICA");

    }


    @Test
    @DisplayName("should update a customer - v2")
    void shouldUpdateACustomerV2(){

        UUID uuid = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .id(uuid)
                .name("Ana")
                .email("ana0@gmail.com")
                .phoneNumber("83987878787")
                .document("05140557070")
                .build();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);

        String document = "16091700081";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";

        UpdateCustomerDtoRequestV2 customerdto = UpdateCustomerDtoRequestV2.builder()
                .name("Ana")
                .email(email)
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .phoneNumber(phoneNumber)
                .document(document)
                .addresses(  addresses  )
                .build();


        BDDMockito.when(customerRepository.findById(uuid)).thenReturn(Optional.ofNullable(customer));


        when( modelMapper.map(customerdto,
                UpdateCustomerDtoRequestV1.class) ).thenReturn(
                UpdateCustomerDtoRequestV1.builder()
                        .name("Ana")
                        .email("ana@gmail.com")
                        .phoneNumber("83999999999")
                        .document("160.917.000-81")
                        .addresses(  addresses  )
                        .build()
        );


        when(customerRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        when(customerRepository.findCustomerByDocument(document))
                .thenReturn(Optional.empty());


        when(customerRepository.findByPhoneNumber(phoneNumber))
                .thenReturn(Optional.empty());


        doNothing().when(addressServiceImpl).deleteAdressesByCustomer(customer);

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();
        AddressDtoResponse response1 = AddressDtoResponse.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoResponse response2 = AddressDtoResponse.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoResponses.add(response1);
        addressDtoResponses.add(response2);

        when(addressServiceImpl.save(addresses, customer))
                .thenReturn(  addressDtoResponses  );

        Customer customerToSave = Customer.builder()
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .build();

        Customer customerSaved = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .build();

        when(customerRepository.save(customerToSave)).thenReturn(customerSaved);


        when( modelMapper.map(any(), eq(CustomerDtoResponseWithAddressesV2.class)) ).thenReturn(
                CustomerDtoResponseWithAddressesV2.builder()
                        .id(uuid)
                        .customerType(CustomerType.FISICA)
                        .name("Ana")
                        .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                        .email("ana@gmail.com")
                        .phoneNumber("83999999999")
                        .document("160.917.000-81")
                        .addresses(  addressDtoResponses  )
                        .build()
        );

        //execução
        customerService.updateV2(uuid, customerdto);

        //verificação
        assert customer != null;
        Mockito.verify(customerRepository, Mockito.times(1)).save(customer);
    }


    @Test
    @DisplayName("should update a customer - v2 - without name")
    void shouldUpdateACustomerV2WithoutName(){

        UUID uuid = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .id(uuid)
                .name("Ana")
                .email("ana0@gmail.com")
                .phoneNumber("83987878787")
                .document("05140557070")
                .build();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);

        String document = "16091700081";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";

        UpdateCustomerDtoRequestV2 customerdto = UpdateCustomerDtoRequestV2.builder()
                //.name("Ana")
                .email(email)
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .phoneNumber(phoneNumber)
                .document(document)
                .addresses(  addresses  )
                .build();


        BDDMockito.when(customerRepository.findById(uuid)).thenReturn(Optional.ofNullable(customer));


        when( modelMapper.map(customerdto,
                UpdateCustomerDtoRequestV1.class) ).thenReturn(
                UpdateCustomerDtoRequestV1.builder()
                        .email("ana@gmail.com")
                        .phoneNumber("83999999999")
                        .document("160.917.000-81")
                        .addresses(  addresses  )
                        .build()
        );


        when(customerRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        when(customerRepository.findCustomerByDocument(document))
                .thenReturn(Optional.empty());


        when(customerRepository.findByPhoneNumber(phoneNumber))
                .thenReturn(Optional.empty());


        doNothing().when(addressServiceImpl).deleteAdressesByCustomer(customer);

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();
        AddressDtoResponse response1 = AddressDtoResponse.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoResponse response2 = AddressDtoResponse.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoResponses.add(response1);
        addressDtoResponses.add(response2);

        when(addressServiceImpl.save(addresses, customer))
                .thenReturn(  addressDtoResponses  );

        Customer customerToSave = Customer.builder()
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .build();

        Customer customerSaved = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .build();

        when(customerRepository.save(customerToSave)).thenReturn(customerSaved);


        when( modelMapper.map(any(), eq(CustomerDtoResponseWithAddressesV2.class)) ).thenReturn(
                CustomerDtoResponseWithAddressesV2.builder()
                        .id(uuid)
                        .customerType(CustomerType.FISICA)
                        .name("Ana")
                        .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                        .email("ana@gmail.com")
                        .phoneNumber("83999999999")
                        .document("160.917.000-81")
                        .addresses(  addressDtoResponses  )
                        .build()
        );

        //execução
        customerService.updateV2(uuid, customerdto);

        //verificação
        assert customer != null;
        Mockito.verify(customerRepository, Mockito.times(1)).save(customer);
    }


    @Test
    @DisplayName("should update a customer - v2 - without birthdate")
    void shouldUpdateACustomerV2WithoutBirthDate(){

        UUID uuid = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .id(uuid)
                .name("Ana")
                .email("ana0@gmail.com")
                .phoneNumber("83987878787")
                .document("05140557070")
                .build();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);

        String document = "16091700081";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";

        UpdateCustomerDtoRequestV2 customerdto = UpdateCustomerDtoRequestV2.builder()
                .name("Ana")
                .email(email)
                //.birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .phoneNumber(phoneNumber)
                .document(document)
                .addresses(  addresses  )
                .build();


        BDDMockito.when(customerRepository.findById(uuid)).thenReturn(Optional.ofNullable(customer));


        when( modelMapper.map(customerdto,
                UpdateCustomerDtoRequestV1.class) ).thenReturn(
                UpdateCustomerDtoRequestV1.builder()
                        .email("ana@gmail.com")
                        .phoneNumber("83999999999")
                        .document("160.917.000-81")
                        .addresses(  addresses  )
                        .build()
        );


        when(customerRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        when(customerRepository.findCustomerByDocument(document))
                .thenReturn(Optional.empty());


        when(customerRepository.findByPhoneNumber(phoneNumber))
                .thenReturn(Optional.empty());


        doNothing().when(addressServiceImpl).deleteAdressesByCustomer(customer);

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();
        AddressDtoResponse response1 = AddressDtoResponse.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoResponse response2 = AddressDtoResponse.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoResponses.add(response1);
        addressDtoResponses.add(response2);

        when(addressServiceImpl.save(addresses, customer))
                .thenReturn(  addressDtoResponses  );

        Customer customerToSave = Customer.builder()
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .build();

        Customer customerSaved = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .build();

        when(customerRepository.save(customerToSave)).thenReturn(customerSaved);


        when( modelMapper.map(any(), eq(CustomerDtoResponseWithAddressesV2.class)) ).thenReturn(
                CustomerDtoResponseWithAddressesV2.builder()
                        .id(uuid)
                        .customerType(CustomerType.FISICA)
                        .name("Ana")
                        .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                        .email("ana@gmail.com")
                        .phoneNumber("83999999999")
                        .document("160.917.000-81")
                        .addresses(  addressDtoResponses  )
                        .build()
        );

        //execução
        customerService.updateV2(uuid, customerdto);

        //verificação
        assert customer != null;
        Mockito.verify(customerRepository, Mockito.times(1)).save(customer);
    }


    @Test
    @DisplayName("should update a customer - v2 - without addresses email, phone number and document")
    void shouldUpdateACustomerV2WithoutAddresses(){

        UUID uuid = UUID.randomUUID();

        String oldEmail = "ana0@gmail.com";
        String oldPhoneNumber = "83987878787";
        String oldDocument = "05140557070";
        Customer customer = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .id(uuid)
                .name("Ana")
                .email(  oldEmail  )
                .phoneNumber(  oldPhoneNumber  )
                .document(  oldDocument  )
                .build();

        String document = "16091700081";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";

        UpdateCustomerDtoRequestV2 customerdto = UpdateCustomerDtoRequestV2.builder()
                .name("Ana")
                //.email(email)
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                //.phoneNumber(phoneNumber)
                //.document(document)
                //.addresses(  addresses  )
                .build();


        BDDMockito.when(customerRepository.findById(uuid)).thenReturn(Optional.ofNullable(customer));


        when( modelMapper.map(customerdto,
                UpdateCustomerDtoRequestV1.class) ).thenReturn(
                UpdateCustomerDtoRequestV1.builder()
                        .name("Ana")
                        .build()
        );


        when(customerRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        when(customerRepository.findCustomerByDocument(document))
                .thenReturn(Optional.empty());


        when(customerRepository.findByPhoneNumber(phoneNumber))
                .thenReturn(Optional.empty());


        doNothing().when(addressServiceImpl).deleteAdressesByCustomer(customer);

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();
        AddressDtoResponse response1 = AddressDtoResponse.builder()
                .cep("58135000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoResponse response2 = AddressDtoResponse.builder()
                .cep("58140000")
                .publicArea("Avenida Rio Branco")
                .district("Bela vista")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoResponses.add(response1);
        addressDtoResponses.add(response2);


        Customer customerToSave = Customer.builder()
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .email(oldEmail)
                .phoneNumber(oldPhoneNumber)
                .document(oldDocument)
                .build();

        Customer customerSaved = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .email(oldEmail)
                .phoneNumber(oldPhoneNumber)
                .document(oldDocument)
                .build();

        when(customerRepository.save(customerToSave)).thenReturn(customerSaved);


        when( modelMapper.map(any(), eq(CustomerDtoResponseWithAddressesV2.class)) ).thenReturn(
                CustomerDtoResponseWithAddressesV2.builder()
                        .id(uuid)
                        .customerType(CustomerType.FISICA)
                        .name("Ana")
                        .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                        .email(oldEmail)
                        .phoneNumber(oldPhoneNumber)
                        .document(oldDocument)
                        .addresses(  addressDtoResponses  )
                        .build()
        );

        //execução
        customerService.updateV2(uuid, customerdto);

        //verificação
        assert customer != null;
        Mockito.verify(customerRepository, Mockito.times(1)).save(customer);
    }
}

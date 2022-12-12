package org.example.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.example.domain.entity.Address;
import org.example.domain.entity.Customer;
import org.example.domain.enums.CustomerType;
import org.example.domain.repository.CustomerRepository;
import org.example.rest.dto_request.AddressDtoRequest;
import org.example.rest.dto_request.CustomerDtoRequest;
import org.example.rest.dto_response.AddressDtoResponse;
import org.example.rest.dto_response.CustomerDtoResponse;
import org.example.rest.dto_response.CustomerDtoResponseWithAdresses;
import org.example.rest.exception.exceptions.DocumentInUseException;
import org.example.rest.exception.exceptions.EmailInUseException;
import org.example.rest.exception.exceptions.EqualValueException;
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
import org.springframework.data.domain.ExampleMatcher;
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

//    @BeforeEach
//    void setUp(){
//        this.customerService = new CustomerServiceImpl(new ModelMapper(), customerRepository, addressServiceImpl);
//    }

    @Test
    @DisplayName("Should save a customer")
    void saveCustomerTest(){

        UUID uuid = UUID.randomUUID();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address2 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addresses.add(address1);
        addresses.add(address2);

        CustomerDtoRequest customerdto = CustomerDtoRequest.builder()
                .customerType(CustomerType.FISICA)
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

        Mockito.doReturn(Optional.empty())
                .when(customerRepository).findCustomerByDocument(customer.getDocument());

        when(modelMapper.map(customerdto, Customer.class)).thenReturn(
                customer
        );


        doNothing().when(addressServiceImpl).save(addresses, customer);

        when( modelMapper.map(customer, CustomerDtoResponse.class) ).thenReturn(
                CustomerDtoResponse.builder()
                        .id(uuid)
                        .customerType(CustomerType.FISICA)
                        .name("Ana")
                        .email("ana@gmail.com")
                        .phoneNumber("83999999999")
                        .document("160.917.000-81")
                        .build()
        );

        when( customerRepository.save( customer ) )
                .thenReturn(
                        Customer.builder()
                                .id(uuid)
                                .customerType(CustomerType.FISICA)
                                .name("Ana")
                                .email("ana@gmail.com")
                                .phoneNumber("83999999999")
                                .document("160.917.000-81")
                                .build()
                );

        //execucao
        CustomerDtoResponse savedCustomer = customerService.save(customerdto);

        //verificacao
        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(savedCustomer.getEmail()).isEqualTo(customerdto.getEmail());
        assertThat(savedCustomer.getDocument()).isEqualTo(customerdto.getDocument());
        assertThat(savedCustomer.getName()).isEqualTo(customerdto.getName());

        //Mockito.verify(customerRepository, Mockito.times(1)).save(customer);
    }

    @Test
    @DisplayName("Should throw DocumentInUseException -> Document already in use")
    void doNotsaveCustomerTest(){

        UUID uuid = UUID.randomUUID();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address2 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addresses.add(address1);
        addresses.add(address2);

        CustomerDtoRequest customerdto = CustomerDtoRequest.builder()
                .customerType(CustomerType.FISICA)
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
        Throwable exception = Assertions.catchThrowable(() -> customerService.save(customerdto));

        //verificacao
        assertThat(exception)
                .isInstanceOf(DocumentInUseException.class)
                .hasMessage("This document is already in use.");

        //Mockito.verify(customerRepository, Mockito.times(1)).save(customer);
    }

    @Test
    @DisplayName("Should throw DocumentInUseException -> Email already in use")
    void doNotsaveCustomerTestEmailInUSe(){

        UUID uuid = UUID.randomUUID();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address2 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addresses.add(address1);
        addresses.add(address2);

        CustomerDtoRequest customerdto = CustomerDtoRequest.builder()
                .customerType(CustomerType.FISICA)
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
        Throwable exception = Assertions.catchThrowable(() -> customerService.save(customerdto));

        //verificacao
        assertThat(exception)
                .isInstanceOf(EmailInUseException.class)
                .hasMessage("This email is already in use. choose another one.");

        //Mockito.verify(customerRepository, Mockito.times(1)).save(customer);
    }

    @Test
    @DisplayName("Should throw DocumentInUseException -> PhoneNumber already in use")
    void doNotsaveCustomerTestPhoneNumberInUSe(){

        UUID uuid = UUID.randomUUID();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address2 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addresses.add(address1);
        addresses.add(address2);

        CustomerDtoRequest customerdto = CustomerDtoRequest.builder()
                .customerType(CustomerType.FISICA)
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
        Throwable exception = Assertions.catchThrowable(() -> customerService.save(customerdto));

        //verificacao
        assertThat(exception)
                .isInstanceOf(PhoneNumberInUseException.class)
                .hasMessage("This phone number is already in use. choose another one.");

        //Mockito.verify(customerRepository, Mockito.times(1)).save(customer);
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
                .document("160.917.000-81")
                .build());

        when( customerRepository.findById( uuid ) )
                .thenReturn(
                        Optional.ofNullable(Customer.builder()
                                .customerType(CustomerType.FISICA)
                                .id(uuid)
                                .name("Ana")
                                .email("ana@gmail.com")
                                .phoneNumber("83999999999")
                                .document("160.917.000-81")
                                .build())
                );

        List<AddressDtoResponse> addressesByCustomer = new ArrayList<>();

        AddressDtoResponse addressDtoResponse = AddressDtoResponse.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addressesByCustomer.add(addressDtoResponse);

        when(addressServiceImpl.getAddressesByCustomer(customer.get())).thenReturn(addressesByCustomer );



        when( modelMapper.map(customer.get(), CustomerDtoResponseWithAdresses.class) ).thenReturn(
                CustomerDtoResponseWithAdresses.builder()
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
        CustomerDtoResponseWithAdresses customerById = customerService.getCustomerById(uuid);

        //verificaçao
        assertThat(customerById.getId()).isEqualTo(uuid);

    }

    @Test
    @DisplayName("should throw error when search a not existent customer by its id")
    void shouldNotFindACustomerByItsId(){

        UUID uuid = UUID.randomUUID();

        when( customerRepository.findById( uuid ) )
                .thenReturn(
                        Optional.empty());

        //execucao
        Throwable exception = Assertions.catchThrowable(()->customerService.getCustomerById(uuid)) ;

        //verificacao
        assertThat(exception)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Customer not found.");

    }



    //erro aqui!!!!!!!!!!!!!!!!!!!!
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
                .document("160.917.000-81")
                .build();

        Customer customerSeacher = Customer.builder()
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .build();

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase("name", "email", "document")
                .withStringMatcher(ExampleMatcher.StringMatcher.STARTING);

        CustomerDtoResponse customerDtoResponse = CustomerDtoResponse.builder()
                .customerType(CustomerType.FISICA)
                .id(uuid)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .build();

        Page<Customer> customerPage = Mockito.mock(Page.class);

        Page<CustomerDtoResponse> responses = new PageImpl<>(List.of(customerDtoResponse), Pageable.ofSize(20), 1);

        when(customerRepository.findAll((Example<Customer>) any(), (Pageable) any())).thenReturn(customerPage);

        when(customerPage.map(customer -> modelMapper.map(customer, CustomerDtoResponse.class)))
                .thenReturn(responses);

        Page<CustomerDtoResponse> page = customerService.searchCustomers("FISICA", "Ana", Pageable.ofSize(20),
                "ana@gmail.com", "83999999999", "160.917.000-81");

        //assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("should delete a customer and their addresses")
    void shouldDeleteAcustomer(){

        UUID uuid = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .name("ana")
                .document("160.917.000-81")
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
                .document("051.405.570-70")
                .build();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);

        String document = "160.917.000-81";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        CustomerDtoRequest customerdto = CustomerDtoRequest.builder()
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .addresses(  addresses  )
                .build();


        BDDMockito.when(customerService.getCustomer(uuid)).thenReturn(Optional.ofNullable(customer));


        when(customerRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        when(customerRepository.findCustomerByDocument(document))
                .thenReturn(Optional.empty());


        when(customerRepository.findByPhoneNumber(phoneNumber))
                .thenReturn(Optional.empty());


        doNothing().when(addressServiceImpl).deleteAdressesByCustomer(customer);

        doNothing().when(addressServiceImpl).save(addresses, customer);

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


        when( modelMapper.map(any(), eq(CustomerDtoResponse.class)) ).thenReturn(
                CustomerDtoResponse.builder()
                        .id(uuid)
                        .customerType(CustomerType.FISICA)
                        .name("Ana")
                        .email("ana@gmail.com")
                        .phoneNumber("83999999999")
                        .document("160.917.000-81")
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

        String document = "160.917.000-81";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);
        CustomerDtoRequest customerdto = CustomerDtoRequest.builder()
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email(email)
                .phoneNumber(phoneNumber)
                .document(document)
                .addresses(  addresses  )
                .build();

        BDDMockito.when(customerService.getCustomer(uuid)).thenReturn(Optional.empty());

        //execução
        org.junit.jupiter.api.Assertions.assertThrows
                (ObjectNotFoundException.class ,() ->customerService.update(uuid, customerdto));

        //verificação
        Mockito.verify(customerRepository, Mockito.never()).save(any());

    }

    @Test
    @DisplayName("should throw exception when email already in use by the customer")
    void shouldThrowExceptionWhenEmailAlreadyInUseByTheCustomer(){

        UUID uuid = UUID.randomUUID();

        String document = "160.917.000-81";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);
        CustomerDtoRequest customerdto = CustomerDtoRequest.builder()
                .customerType(CustomerType.FISICA)
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

        BDDMockito.when(customerService.getCustomer(uuid)).thenReturn(Optional.of(customerById));


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

        String document = "160.917.000-81";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);
        CustomerDtoRequest customerdto = CustomerDtoRequest.builder()
                .customerType(CustomerType.FISICA)
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

        BDDMockito.when(customerService.getCustomer(uuid)).thenReturn(Optional.of(customerById));


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

        String document = "160.917.000-81";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);
        CustomerDtoRequest customerdto = CustomerDtoRequest.builder()
                .customerType(CustomerType.FISICA)
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
                .document("629.421.540-47")
                .build();

        BDDMockito.when(customerService.getCustomer(uuid)).thenReturn(Optional.of(customerById));


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

        String document = "160.917.000-81";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);
        CustomerDtoRequest customerdto = CustomerDtoRequest.builder()
                .customerType(CustomerType.FISICA)
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

        BDDMockito.when(customerService.getCustomer(uuid)).thenReturn(Optional.of(customerById));

        Customer customerWithSameEmail = Customer.builder()
                .id(UUID.randomUUID())
                .customerType(CustomerType.FISICA)
                .name("joao")
                .email(email)
                .phoneNumber("83955554444")
                .document("244.876.270-87")
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

        String document = "160.917.000-81";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);
        CustomerDtoRequest customerdto = CustomerDtoRequest.builder()
                .customerType(CustomerType.FISICA)
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

        BDDMockito.when(customerService.getCustomer(uuid)).thenReturn(Optional.of(customerById));

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

        String document = "160.917.000-81";
        String phoneNumber = "83999999999";
        String email = "ana@gmail.com";
        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);
        CustomerDtoRequest customerdto = CustomerDtoRequest.builder()
                .customerType(CustomerType.FISICA)
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

        BDDMockito.when(customerService.getCustomer(uuid)).thenReturn(Optional.of(customerById));

        Customer customerWithSamePhone = Customer.builder()
                .id(UUID.randomUUID())
                .customerType(CustomerType.FISICA)
                .name("joao")
                .phoneNumber(phoneNumber)
                .email("emaildiferente@gmail.com")
                .document("244.876.270-87")
                .build();


        when(customerRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(customerWithSamePhone));

        //execução
        org.junit.jupiter.api.Assertions.assertThrows
                (PhoneNumberInUseException.class ,() -> customerService.update(uuid, customerdto));

        //verificação
        Mockito.verify(customerRepository, Mockito.never()).save(any());
    }

}

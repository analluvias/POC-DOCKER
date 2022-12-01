package org.example.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.domain.entity.Customer;
import org.example.domain.enums.CustomerType;
import org.example.domain.repository.CustomerRepository;
import org.example.rest.dto_request.AddressDtoRequest;
import org.example.rest.dto_request.CustomerDtoRequest;
import org.example.rest.dto_response.CustomerDtoResponse;
import org.example.service.impl.AddressServiceImpl;
import org.example.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;


//@SpringBootTest
//@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    void setUp(){
        this.customerService = new CustomerServiceImpl(new ModelMapper(), customerRepository, addressServiceImpl);
    }

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
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .build();

        Mockito.doReturn(Optional.empty())
                .when(customerRepository).findCustomerByDocument(customer.getDocument());

        Mockito.when(modelMapper.map(customerdto, Customer.class)).thenReturn(
                customer
        );


        doNothing().when(addressServiceImpl).save(addresses, customer);

        Mockito.when( modelMapper.map(customer, CustomerDtoResponse.class) ).thenReturn(
                CustomerDtoResponse.builder()
                        .id(uuid)
                        .customerType(CustomerType.FISICA)
                        .name("Ana")
                        .email("ana@gmail.com")
                        .phoneNumber("83999999999")
                        .document("160.917.000-81")
                        .build()
        );

        Mockito.when( customerRepository.save( customer ) )
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

}

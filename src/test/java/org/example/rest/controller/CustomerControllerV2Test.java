package org.example.rest.controller;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.example.domain.entity.Customer;
import org.example.domain.enums.CustomerType;
import org.example.rest.dto_request.AddressDtoRequest;
import org.example.rest.dto_request.CustomerDtoRequestV2;
import org.example.rest.dto_request.UpdateCustomerDtoRequestV2;
import org.example.rest.dto_response.AddressDtoResponse;
import org.example.rest.dto_response.CustomerDtoResponseV2;
import org.example.rest.dto_response.CustomerDtoResponseWithAddressesV1;
import org.example.rest.dto_response.CustomerDtoResponseWithAddressesV2;
import org.example.service.impl.AddressServiceImpl;
import org.example.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = CustomerControllerV2.class)
@AutoConfigureMockMvc
public class CustomerControllerV2Test {

    static String CUSTOMER_API = "/v2/api/customers";

    @Autowired
    MockMvc mvc;

    @MockBean
    CustomerServiceImpl customerServiceImpl;

    @MockBean
    AddressServiceImpl addressServiceImpl;

    @Test
    @DisplayName("Shoud create a customer")
    void createCustomerTest() throws Exception {

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("avenida rio branco")
                .district("bela vista")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .cep("58417290")
                .publicArea("Avenida Francisco Lopes de Almeida")
                .district("Santa Cruz")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();

        AddressDtoResponse response1= AddressDtoResponse.builder()
                .id(UUID.randomUUID().toString())
                .cep("58135000")
                .houseNumber("1233")
                .mainAddress(true)
                .city("campina grande")
                .state("PB")
                .build();

        AddressDtoResponse response2= AddressDtoResponse.builder()
                .id(UUID.randomUUID().toString())
                .cep("58417290")
                .houseNumber("12")
                .mainAddress(false)
                .city("campina grande")
                .state("PB")
                .build();

        addressDtoResponses.add(response1);
        addressDtoResponses.add(response2);

        CustomerDtoRequestV2 dto = CustomerDtoRequestV2.builder()
                .name("ana livia")
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .email("emailteste@gmail.com")
                .phoneNumber("83999999999")
                .customerType(String.valueOf(CustomerType.FISICA))
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        CustomerDtoResponseWithAddressesV2 customerDtoResponse = CustomerDtoResponseWithAddressesV2.builder()
                .customerType(CustomerType.FISICA)
                .email("emailteste@gmail.com")
                .name("ana livia")
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .addresses( addressDtoResponses )
                .build();

        BDDMockito.when( customerServiceImpl.existsCustomersByDocument(customerDtoResponse.getDocument()) )
                .thenReturn(false);

        BDDMockito.given( customerServiceImpl.saveV2( dto ) ).willReturn( customerDtoResponse );


        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        mapper.registerModule(module);

        String json = mapper.writeValueAsString( dto );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc
                .perform( request )
                .andExpect( MockMvcResultMatchers.status().isCreated() )
                .andExpect( jsonPath("customerType")
                        .value( dto.getCustomerType().toString() ) )
                .andExpect( jsonPath("email").
                        value( dto.getEmail() ))
                .andExpect( jsonPath("name").
                        value( dto.getName() ))
                .andExpect(  jsonPath("phoneNumber").
                        value( dto.getPhoneNumber() ))
                .andExpect( jsonPath("document").
                        value( dto.getDocument() ))
                .andExpect(jsonPath("addresses").exists());


    }


    @Test
    @DisplayName("should get a customer by its id")
    void shouldGetACustomerById() throws Exception {

        UUID id = UUID.randomUUID();

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();

        AddressDtoResponse addressDtoResponse = AddressDtoResponse.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addressDtoResponses.add(addressDtoResponse);

        CustomerDtoResponseWithAddressesV1 customer = CustomerDtoResponseWithAddressesV1.builder()
                .email("ana@gmail.com")
                .id(id)
                .customerType(  CustomerType.FISICA  )
                .document("160.917.000-81")
                .addresses(  addressDtoResponses  )
                .build();

        BDDMockito.given( customerServiceImpl.getCustomerByIdV1(id) ).willReturn( customer );


        //execução (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CUSTOMER_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // verificação
        mvc.perform( request )
                .andExpect( status().isOk() );
    }


    @Test
    @DisplayName("Should search a customer by properties ")
    void shouldSearchACustomer() throws Exception {

        UUID id = UUID.randomUUID();

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();

        AddressDtoResponse addressDtoResponse = AddressDtoResponse.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addressDtoResponses.add(addressDtoResponse);

        CustomerDtoResponseV2 customer = CustomerDtoResponseV2.builder()
                .name("Ana Lívia Meira")
                .email("ana@gmail.com")
                .id(id)
                .customerType(  CustomerType.FISICA  )
                .document("160.917.000-81")
                .build();

        Page<CustomerDtoResponseV2> page = new PageImpl<>(List.of(customer), Pageable.ofSize(20), 0);


        BDDMockito.given( customerServiceImpl.searchCustomersV2(null, "ana", Pageable.ofSize(10),
                        null, null, null, null) )
                .willReturn(page);

        //execução (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CUSTOMER_API.concat("/searchcustomers?name=a"))
                .accept(MediaType.APPLICATION_JSON);


        // verificação
        mvc.perform( request )
                .andExpect( status().isOk() );
    }


    @Test
    @DisplayName("should delete a customer and their addresses")
    void shouldDeleteACustomerAndHisAddresses() throws Exception {

        UUID uuid = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(uuid)
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .customerType(CustomerType.FISICA)
                .name("ana")
                .document("160.917.000-81")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .build();

        BDDMockito.given( customerServiceImpl.getCustomer(uuid) )
                .willReturn(customer);

        doNothing().when(addressServiceImpl).deleteAdressesByCustomer(customer);

        doNothing().when(customerServiceImpl).delete(uuid);

        //execução (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(CUSTOMER_API + "/" + uuid)
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("Shoud update a customer")
    void updateCustomerTest() throws Exception {

        UUID uuid = UUID.randomUUID();

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .cep("58.140-000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        UpdateCustomerDtoRequestV2 dto = UpdateCustomerDtoRequestV2.builder()
                .name("ana livia")
                .email("emailteste@gmail.com")
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        List<AddressDtoResponse> addressDtoResponses = new ArrayList<>();

        AddressDtoResponse response1= AddressDtoResponse.builder()
                .cep("58135000")
                .houseNumber("1233")
                .mainAddress(true)
                .city("esperança")
                .state("PB")
                .build();

        addressDtoResponses.add(response1);

        CustomerDtoResponseWithAddressesV2 customerDtoResponse = CustomerDtoResponseWithAddressesV2.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .email("emailteste@gmail.com")
                .birthDate( LocalDate.of(2020, Month.JANUARY, 8) )
                .name("ana livia")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .addresses(addressDtoResponses)
                .build();

        BDDMockito.given( customerServiceImpl.updateV2(uuid, dto ) ).willReturn( customerDtoResponse );

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        mapper.registerModule(module);

        String json = mapper.writeValueAsString( dto );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(CUSTOMER_API + "/" + uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc
                .perform( request )
                .andExpect( MockMvcResultMatchers.status().isOk() )
                .andExpect(jsonPath("id").hasJsonPath());

    }
}

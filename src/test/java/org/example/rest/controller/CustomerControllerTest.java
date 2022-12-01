package org.example.rest.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.example.domain.enums.CustomerType;
import org.example.rest.dto_request.AddressDtoRequest;
import org.example.rest.dto_request.CustomerDtoRequest;
import org.example.rest.dto_response.CustomerDtoResponse;
import org.example.rest.exception.exceptions.DocumentInUseException;
import org.example.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@WebMvcTest(controllers = CustomerController.class)
@AutoConfigureMockMvc
class CustomerControllerTest {

    static String CUSTOMER_API = "/api/customer";

    @Autowired
    MockMvc mvc;

    @MockBean
    CustomerServiceImpl customerServiceImpl;

    @Test
    @DisplayName("Shoud create book")
    void createCustomerTest() throws Exception {

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        CustomerDtoRequest dto = CustomerDtoRequest.builder()
                .name("ana livia")
                .email("emailteste@gmail.com")
                .phoneNumber("83999999999")
                .customerType(CustomerType.FISICA)
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        CustomerDtoResponse customerDtoResponse = CustomerDtoResponse.builder()
                .customerType(CustomerType.FISICA)
                .email("emailteste@gmail.com")
                .name("ana livia")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .build();

        BDDMockito.when( customerServiceImpl.existsCustomersByDocument(customerDtoResponse.getDocument()) )
                        .thenReturn(false);

        BDDMockito.given( customerServiceImpl.save( dto ) ).willReturn( customerDtoResponse );

        String json = new ObjectMapper().writeValueAsString( dto );

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
                        value( dto.getDocument() ));


    }

    @Test
    @DisplayName("Should throw validation error when email is empty")
    void createInvalidCustomerTestWithoutEmail() throws Exception{

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        CustomerDtoRequest dto = CustomerDtoRequest.builder()
                .name("ana")
                .phoneNumber("83999999999")
                .customerType(CustomerType.FISICA)
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        String json = new ObjectMapper().writeValueAsString( dto );

        // mockando a requisição HTTP para o meu método verdadeiro
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect(jsonPath("message").value("Email cannot be empty"));
    }

    @Test
    @DisplayName("Should throw validation error when name is empty")
    void createInvalidCustomerTestWithoutName() throws Exception{

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        CustomerDtoRequest dto = CustomerDtoRequest.builder()
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .customerType(CustomerType.FISICA)
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        String json = new ObjectMapper().writeValueAsString( dto );

        // mockando a requisição HTTP para o meu método verdadeiro
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect(jsonPath("message").value("Name cannot be empty"));
    }

    @Test
    @DisplayName("Should throw validation error when phone number is empty")
    void createInvalidCustomerTestWithoutPhoneNumber() throws Exception{

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        CustomerDtoRequest dto = CustomerDtoRequest.builder()
                .name("ana")
                .email("ana@gmail.com")
                .customerType(CustomerType.FISICA)
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        String json = new ObjectMapper().writeValueAsString( dto );

        // mockando a requisição HTTP para o meu método verdadeiro
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect(jsonPath("message").value("Phone number cannot be empty"));
    }

    @Test
    @DisplayName("Should throw validation error when document is empty")
    void createInvalidCustomerTestWithoutDocument() throws Exception{

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        CustomerDtoRequest dto = CustomerDtoRequest.builder()
                .name("ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .customerType(CustomerType.FISICA)
                //.document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        String json = new ObjectMapper().writeValueAsString( dto );

        // mockando a requisição HTTP para o meu método verdadeiro
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect(jsonPath("message").value("document cannot be empty"));
    }

    @Test
    @DisplayName("Should throw validation error when document is already in use")
    void createInvalidCustomerTestDocumentInUse() throws Exception{

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        CustomerDtoRequest dto = CustomerDtoRequest.builder()
                .name("ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .customerType(CustomerType.FISICA)
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        BDDMockito.given( customerServiceImpl.save( Mockito.any(CustomerDtoRequest.class) ) )
                .willThrow( new DocumentInUseException());

        String json = new ObjectMapper().writeValueAsString( dto );

        // mockando a requisição HTTP para o meu método verdadeiro
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc.perform(request)
                .andExpect( status().isConflict() )
                .andExpect(jsonPath("message").value("This document is already in use."));
    }

}

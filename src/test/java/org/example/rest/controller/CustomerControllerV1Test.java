package org.example.rest.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.example.domain.entity.Customer;
import org.example.domain.enums.CustomerType;
import org.example.rest.dto_request.AddressDtoRequest;
import org.example.rest.dto_request.CustomerDtoRequestV1;
import org.example.rest.dto_request.UpdateCustomerDtoRequestV1;
import org.example.rest.dto_response.AddressDtoResponse;
import org.example.rest.dto_response.CustomerDtoResponseV1;
import org.example.rest.dto_response.CustomerDtoResponseWithAddressesV1;
import org.example.rest.exception.exceptions.DocumentInUseException;
import org.example.rest.exception.exceptions.ObjectNotFoundException;
import org.example.rest.exception.exceptions.TooManyAddressesException;
import org.example.service.impl.AddressServiceImpl;
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
@WebMvcTest(controllers = CustomerControllerV1.class)
@AutoConfigureMockMvc
class CustomerControllerV1Test {

    static String CUSTOMER_API = "/v1/api/customers";

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

        CustomerDtoRequestV1 dto = CustomerDtoRequestV1.builder()
                .name("ana livia")
                .email("emailteste@gmail.com")
                .phoneNumber("83999999999")
                .customerType("FISICA")
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        CustomerDtoResponseWithAddressesV1 customerDtoResponse = CustomerDtoResponseWithAddressesV1.builder()
                .customerType(CustomerType.FISICA)
                .email("emailteste@gmail.com")
                .name("ana livia")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .addresses( addressDtoResponses )
                .build();

        BDDMockito.when( customerServiceImpl.existsCustomersByDocument(customerDtoResponse.getDocument()) )
                        .thenReturn(false);

        BDDMockito.given( customerServiceImpl.saveV1( dto ) ).willReturn( customerDtoResponse );

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
                        value( dto.getDocument() ))
                .andExpect(jsonPath("addresses").exists());


    }

    @Test
    @DisplayName("must not create a customer with many addresse")
    void dontCreateCustomerTest() throws Exception {

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .cep("58140000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        AddressDtoRequest addressDtoRequest3 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest4 = AddressDtoRequest.builder()
                .cep("58140000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        AddressDtoRequest addressDtoRequest5 = AddressDtoRequest.builder()
                .cep("58135000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest6 = AddressDtoRequest.builder()
                .cep("58140000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);
        addressDtoRequests.add(addressDtoRequest3);
        addressDtoRequests.add(addressDtoRequest4);
        addressDtoRequests.add(addressDtoRequest5);
        addressDtoRequests.add(addressDtoRequest6);

        CustomerDtoRequestV1 dto = CustomerDtoRequestV1.builder()
                .name("ana livia")
                .email("emailteste@gmail.com")
                .phoneNumber("83999999999")
                .customerType("FISICA")
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        BDDMockito.given( customerServiceImpl.saveV1( dto ) )
                .willThrow( new TooManyAddressesException());

        String json = new ObjectMapper().writeValueAsString( dto );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc
                .perform( request )
                .andExpect( MockMvcResultMatchers.status().isBadRequest() );

    }

    @Test
    @DisplayName("Should throw validation error when email is empty")
    void createInvalidCustomerTestWithoutEmail() throws Exception{

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest addressDtoRequest1 = AddressDtoRequest.builder()
                .cep("58.135-000")
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

        CustomerDtoRequestV1 dto = CustomerDtoRequestV1.builder()
                .name("ana")
                .phoneNumber("83999999999")
                .customerType("FISICA")
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
                .cep("58135000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDtoRequest2 = AddressDtoRequest.builder()
                .cep("58140000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressDtoRequests.add(addressDtoRequest1);
        addressDtoRequests.add(addressDtoRequest2);

        CustomerDtoRequestV1 dto = CustomerDtoRequestV1.builder()
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .customerType("FISICA")
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
                .cep("58.135-000")
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

        CustomerDtoRequestV1 dto = CustomerDtoRequestV1.builder()
                .name("ana")
                .email("ana@gmail.com")
                .customerType("FISICA")
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
                .cep("58.135-000")
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

        CustomerDtoRequestV1 dto = CustomerDtoRequestV1.builder()
                .name("ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .customerType("FISICA")
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
                .cep("58.135-000")
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

        CustomerDtoRequestV1 dto = CustomerDtoRequestV1.builder()
                .name("ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .customerType("FISICA")
                .document("160.917.000-81")
                .addresses(  addressDtoRequests  )
                .build();

        BDDMockito.given( customerServiceImpl.saveV1( Mockito.any(CustomerDtoRequestV1.class) ) )
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
    @DisplayName("should return error when the customer does not exist")
    void ErrorCustomerDoesNotExist() throws Exception {

        UUID id = UUID.randomUUID();

        BDDMockito.given( customerServiceImpl.getCustomerByIdV1(id) )
                .willThrow( new ObjectNotFoundException("Customer not found."));


        //execução (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CUSTOMER_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // verificação
        mvc.perform( request )
                .andExpect( status().isBadRequest() );
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

        CustomerDtoResponseV1 customer = CustomerDtoResponseV1.builder()
                .name("Ana Lívia Meira")
                .email("ana@gmail.com")
                .id(id)
                .customerType(  CustomerType.FISICA  )
                .document("160.917.000-81")
                .build();

        Page<CustomerDtoResponseV1> page = new PageImpl<>(List.of(customer), Pageable.ofSize(20), 0);


        BDDMockito.given( customerServiceImpl.searchCustomers(null, "ana", Pageable.ofSize(10),
                        null, null, null) )
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
    @DisplayName("should throw error when try to delete a nonexistent customer")
    void shouldNotDeleteACustomerInexistentAndHisAddresses() throws Exception {

        UUID uuid = UUID.randomUUID();


        doThrow( new ObjectNotFoundException("the customer you tried to delete does not exist.") )
                .when( customerServiceImpl ).delete(uuid);


        //execução (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(CUSTOMER_API + "/" + uuid)
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest());
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

        UpdateCustomerDtoRequestV1 dto = UpdateCustomerDtoRequestV1.builder()
                .name("ana livia")
                .email("emailteste@gmail.com")
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

        CustomerDtoResponseWithAddressesV1 customerDtoResponse = CustomerDtoResponseWithAddressesV1.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .email("emailteste@gmail.com")
                .name("ana livia")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .addresses(addressDtoResponses)
                .build();

        BDDMockito.given( customerServiceImpl.update(uuid, dto ) ).willReturn( customerDtoResponse );

        String json = new ObjectMapper().writeValueAsString( dto );

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

package org.example.rest.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.example.domain.entity.Address;
import org.example.domain.entity.Customer;
import org.example.rest.dto_request.AddressDtoUpdateRequest;
import org.example.rest.dto_response.AddressDtoResponse;
import org.example.rest.exception.exceptions.CepShouldHaveStateAndCityException;
import org.example.rest.exception.exceptions.MustHaveAtLeastOneMainAddres;
import org.example.rest.exception.exceptions.ObjectNotFoundException;
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
@WebMvcTest(controllers = AddressController.class)
@AutoConfigureMockMvc
class AddressControllerTest {

    static String ADDRESS_API = "/v1/api/addresses";

    @Autowired
    MockMvc mvc;

    @MockBean
    AddressServiceImpl addressService;

    @MockBean
    CustomerServiceImpl customerServiceImpl;


    @Test
    @DisplayName("should delete a address by id")
    void shouldDeleteAddessById() throws Exception {

        UUID uuid = UUID.randomUUID();

        Address addressToDelete = Address.builder()
                .id(uuid)
                .cep("58400530")
                .state("PB")
                .city("Campina Grande")
                .publicArea("Avenida rio branco")
                .district("Bela Vista")
                .houseNumber("1255")
                .mainAddress(false)
                .build();

        BDDMockito.doNothing().when(addressService)
                .deleteById(uuid);

        BDDMockito.given(addressService.getAddressById(uuid))
                .willReturn(addressToDelete);


        //execução (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(ADDRESS_API + "/" + uuid)
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("should throw exception when try to delete nonexistent address")
    void shouldThrowExceptionWhenTryToDeleteNonExistentAddress() throws Exception {

        UUID uuid = UUID.randomUUID();

        Address addressToDelete = Address.builder()
                .id(uuid)
                .cep("58400530")
                .state("PB")
                .city("Campina Grande")
                .publicArea("Avenida rio branco")
                .district("Bela Vista")
                .houseNumber("1255")
                .mainAddress(false)
                .build();

        BDDMockito.doThrow(new MustHaveAtLeastOneMainAddres()).when(addressService)
                .deleteById(uuid);


        //execução (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(ADDRESS_API + "/" + uuid)
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Shoud update the main address")
    void updateMainAddress() throws Exception {

        UUID uuid = UUID.randomUUID();

        AddressDtoResponse addressDtoResponse = AddressDtoResponse.builder()
                .cep("58135000")
                .state("PB")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        BDDMockito.given( addressService.update(uuid) ).willReturn( addressDtoResponse );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(ADDRESS_API + "/setmainaddress/" + uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform( request )
                .andExpect( MockMvcResultMatchers.status().isOk() )
                .andExpect(jsonPath("mainAddress").hasJsonPath());


    }

    @Test
    @DisplayName("Shoud throw error when try to update nonexistent main address")
    void throwErrorWhenTryToUpdateNonexistentMainAddress() throws Exception {

        UUID uuid = UUID.randomUUID();

        AddressDtoResponse addressDtoResponse = AddressDtoResponse.builder()
                .cep("58135000")
                .state("PB")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        BDDMockito.doThrow( new ObjectNotFoundException("This address does not exist on DB.") )
                .when(addressService).update(uuid);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(ADDRESS_API + "/setmainaddress/" + uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform( request )
                .andExpect( MockMvcResultMatchers.status().isBadRequest() );


    }

    @Test
    @DisplayName("Shoud update the address by id")
    void updateAddressById() throws Exception {

        UUID uuid = UUID.randomUUID();

        AddressDtoUpdateRequest dtoRequest = AddressDtoUpdateRequest.builder()
                .cep("58135000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("1255")
                .build();

        AddressDtoResponse response = AddressDtoResponse.builder()
                .state("PB")
                .city("Campina Grande")
                .cep("58135000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("1255")
                .build();

        String json = new ObjectMapper().writeValueAsString( dtoRequest );

        BDDMockito.when( addressService.updateAddress(uuid, dtoRequest) )
                .thenReturn(response);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(ADDRESS_API + "/" + uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc
                .perform( request )
                .andExpect( MockMvcResultMatchers.status().isOk());

    }

    @Test
    @DisplayName("Shoud throw exception when try to" +
            " update nonExistent address by id")
    void throwExceptionWhenTryToUpdateNonExistentAddressById() throws Exception {

        UUID uuid = UUID.randomUUID();

        AddressDtoUpdateRequest dtoRequest = AddressDtoUpdateRequest.builder()
                .cep("58135000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("1255")
                .build();

        AddressDtoResponse response = AddressDtoResponse.builder()
                .state("PB")
                .city("Campina Grande")
                .cep("58135000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("1255")
                .build();

        String json = new ObjectMapper().writeValueAsString( dtoRequest );

        BDDMockito.doThrow( new ObjectNotFoundException("This address does not exist on DB.") )
                .when(addressService).updateAddress(uuid, dtoRequest);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(ADDRESS_API + "/" + uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc
                .perform( request )
                .andExpect( MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Shoud throw exception when try to" +
            " update address with nonValid cep")
    void throwExceptionWhenTryToUpdateAddressWithNonValidCep() throws Exception {

        UUID uuid = UUID.randomUUID();

        AddressDtoUpdateRequest dtoRequest = AddressDtoUpdateRequest.builder()
                .cep("58135000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("1255")
                .build();

        AddressDtoResponse response = AddressDtoResponse.builder()
                .state("PB")
                .city("Campina Grande")
                .cep("58135000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("1255")
                .build();

        String json = new ObjectMapper().writeValueAsString( dtoRequest );

        BDDMockito.doThrow( new CepShouldHaveStateAndCityException() )
                .when(addressService).updateAddress(uuid, dtoRequest);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(ADDRESS_API + "/" + uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(  json  );

        mvc
                .perform( request )
                .andExpect( MockMvcResultMatchers.status().isBadRequest());
    }



    @Test
    @DisplayName("should get a address by customer id")
    void shouldGetAddressesByCustomerId() throws Exception {

        UUID uuid = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(uuid)
                .build();

        BDDMockito.given(customerServiceImpl.getCustomer(uuid))
                .willReturn(customer);

        List<AddressDtoResponse> addressesToReturn = new ArrayList<>();
        AddressDtoResponse addressToReturn = AddressDtoResponse.builder()
                .state("PB")
                .city("Campina Grande")
                .cep("58135000")
                .publicArea("Centro")
                .district("Centro")
                .houseNumber("1255")
                .build();
        addressesToReturn.add(addressToReturn);

        BDDMockito.given(addressService.getAddressesByCustomer(customer))
                .willReturn(addressesToReturn);


        //execução (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(ADDRESS_API.concat("?customerId=" + uuid))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk());
    }
}

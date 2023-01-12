package org.example.rest.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rest.dto_request.CustomerDtoRequestV1;
import org.example.rest.dto_request.UpdateCustomerDtoRequestV1;
import org.example.rest.dto_response.CustomerDtoResponseV1;
import org.example.rest.dto_response.CustomerDtoResponseWithAddressesV1;
import org.example.service.impl.CustomerServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/customers")
@RequiredArgsConstructor
public class CustomerControllerV1 {

    private final CustomerServiceImpl service;

    @Caching(evict = {
            @CacheEvict(value ="customerByIdV1", allEntries = true),
            @CacheEvict(value = "searchCustomerV1", allEntries = true)
    })
    @ApiOperation(value = "Create a customer.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "successfully created customer"),
            @ApiResponse(code = 400, message = "An exception was generated"),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDtoResponseWithAddressesV1 save(@RequestBody @Valid CustomerDtoRequestV1 request){

        return service.saveV1(  request  );

    }

    @Cacheable(value = "customerByIdV1", key = "#id")
    @ApiOperation(value = "Get customers by id.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "customer found successfully"),
            @ApiResponse(code = 400, message = "An exception was generated"),
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDtoResponseWithAddressesV1 getCustomerById(@PathVariable("id") UUID id){

        return service.getCustomerByIdV1(id);

    }


    @Cacheable(value = "searchCustomerV1")
    @ApiOperation(value = "Search customers by customer type, name, email, phone number, " +
            "or document.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successfully searched customer"),
            @ApiResponse(code = 400, message = "An exception was generated"),
    })
    @GetMapping("/searchcustomers")
    @ResponseStatus(HttpStatus.OK)
    public Page<CustomerDtoResponseV1> searchCustomers(
            Pageable pageable,
            @RequestParam(name = "customerType", required = false) String customerType,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(name = "document", required = false) String document)

    {
        return service.searchCustomers(customerType, name,
                pageable, email, phoneNumber, document);
    }

    @Caching(evict = {
            @CacheEvict(value ="customerByIdV1", key = "#id"),
            @CacheEvict(value = "searchCustomerV1", allEntries = true),
            @CacheEvict(value ="addressesById", key = "#id")
    })
    @ApiOperation(value = "delete an customer by id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Customer deleted successfully"),
            @ApiResponse(code = 400, message = "An exception was generated"),
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete (@PathVariable("id") UUID id){

        service.delete(id);
    }

    @Caching(evict = {
            @CacheEvict(value ="customerByIdV1", key = "#id"),
            @CacheEvict(value = "searchCustomerV1", allEntries = true),
            @CacheEvict(value ="addressesById", key = "#id")
    })
    @ApiOperation(value = "Update the customer with addresses by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "customer updated successfully"),
            @ApiResponse(code = 400, message = "An exception was generated"),
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDtoResponseWithAddressesV1 patchCustomer(@PathVariable("id") UUID id,
                                                            @RequestBody UpdateCustomerDtoRequestV1 request){

        return service.update(id, request);
    }

}

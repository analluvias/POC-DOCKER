package org.example.rest.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.rest.dto_request.AddressDtoUpdateRequest;
import org.example.rest.dto_response.AddressDtoResponse;
import org.example.service.impl.AddressServiceImpl;
import org.example.service.impl.CustomerServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressServiceImpl addressService;
    private final CustomerServiceImpl customerService;

    @CacheEvict(value = {"customerByIdV1", "addressesByIdV1", "customerByIdV2", "addressesByIdV2"}, allEntries = true)
    @Caching(evict = {
            @CacheEvict(value ="customerById", key = "#id"),
            @CacheEvict(value ="addressesById", key = "#id")
    })
    @ApiOperation(value = "delete an address by id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Address deleted successfully"),
            @ApiResponse(code = 400, message = "An exception was generated"),
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete (@PathVariable("id") UUID id){

        addressService.deleteById(id);
    }

    @CacheEvict(value = {"customerByIdV1", "addressesByIdV1", "customerByIdV2", "addressesByIdV2"}, allEntries = true)
    @ApiOperation(value = "Change the main address by the id of the desired new main address")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Main address changed successfully"),
            @ApiResponse(code = 400, message = "An exception was generated"),
    })
    @PatchMapping("/setmainaddress/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AddressDtoResponse updateMainAddress(@PathVariable("id") UUID id){

        return addressService.update(id);
    }

    @CacheEvict(value = {"customerByIdV1", "addressesByIdV1", "customerByIdV2", "addressesByIdV2"}, allEntries = true)
    @ApiOperation(value = "Update address data by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Address data changed successfully"),
            @ApiResponse(code = 400, message = "An exception was generated"),
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AddressDtoResponse updateAddress(@PathVariable("id") UUID id, @RequestBody AddressDtoUpdateRequest request){

        return addressService.updateAddress(id, request);
    }


    @Cacheable(value = "addressesById", key = "#id")
    @ApiOperation(value = "Get addresses by customer id.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "addresses found successfully"),
            @ApiResponse(code = 400, message = "An exception was generated"),
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<AddressDtoResponse> getAddressesByCustomerId(@RequestParam(name = "customerId") UUID id){

        return addressService
                .getAddressesByCustomer( customerService.getCustomer(id) );
    }
}

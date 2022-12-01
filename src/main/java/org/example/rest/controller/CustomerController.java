package org.example.rest.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.rest.dto_request.CustomerDtoRequest;
import org.example.rest.dto_response.CustomerDtoResponse;
import org.example.service.impl.CustomerServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerServiceImpl service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDtoResponse save(@RequestBody @Valid CustomerDtoRequest request){

        return service.save(  request  );

    }

//    @GetMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<CustomerDtoRequest> teste(){
//
//        AddressDtoRequest list = AddressDtoRequest.builder()
//                .cep("123")
//                .state("a")
//                .district("a")
//                .houseNumber("122")
//                .street("a")
//                .build();
//
//        CustomerDtoRequest a = CustomerDtoRequest.builder()
//                .customerType(CustomerType.FISICA)
//                .addresses(List.of(list))
//                .build();
//
//        return ResponseEntity.ok(a);
//    }
}

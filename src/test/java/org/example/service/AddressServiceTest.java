package org.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.example.domain.entity.Address;
import org.example.domain.entity.Customer;
import org.example.domain.enums.CustomerType;
import org.example.domain.repository.AddressRepository;
import org.example.rest.dto_request.AddressDtoRequest;
import org.example.service.impl.AddressServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class AddressServiceTest {
    @InjectMocks
    AddressServiceImpl addressService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AddressRepository addressRepository;

    @Test
    @DisplayName("should save a address")
    void shouldSaveAddress(){

        Customer customer = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(UUID.randomUUID())
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
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

        Address addressToSave1 = Address.builder()
                .state("paraíba")
                .cep("58.135-000")
                .customer(customer)
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        Address addressToSave2 = Address.builder()
                .state("paraíba")
                .cep("58.140-000")
                .customer(customer)
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        Mockito.when(modelMapper.map(address1, Address.class)).thenReturn(
                addressToSave1
        );

        Mockito.when(modelMapper.map(address2, Address.class)).thenReturn(
                addressToSave2
        );

        List<Address> addressesToSave = new ArrayList<>();
        addressesToSave.add(addressToSave1);
        addressesToSave.add(addressToSave2);

        List<Address> addressesToReturn = new ArrayList<>();

        Address addressToReturn1 = Address.builder()
                .id(UUID.randomUUID())
                .state("paraíba")
                .cep("58.135-000")
                .customer(customer)
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        Address addressToReturn2 = Address.builder()
                .id(UUID.randomUUID())
                .state("paraíba")
                .cep("58.140-000")
                .customer(customer)
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressesToReturn.add(addressToReturn1);
        addressesToReturn.add(addressToReturn2);


        Mockito.when( addressRepository.saveAll( addressesToSave ) )
                .thenReturn(
                        addressesToReturn
                );

        //execucao
        addressService.save(addresses, customer);

        //verificacao
        Mockito.verify(addressRepository, Mockito.times(1))
                .saveAll(addressesToSave);

    }

}

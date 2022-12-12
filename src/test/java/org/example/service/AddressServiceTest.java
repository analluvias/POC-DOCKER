package org.example.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.example.domain.entity.Address;
import org.example.domain.entity.Customer;
import org.example.domain.enums.CustomerType;
import org.example.domain.repository.AddressRepository;
import org.example.rest.dto_request.AddressDtoRequest;
import org.example.rest.dto_response.AddressDtoResponse;
import org.example.rest.exception.exceptions.MustHaveAtLeastOneMainAddres;
import org.example.rest.exception.exceptions.TooManyAddressesException;
import org.example.rest.exception.exceptions.TooManyMainAddressesException;
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

    @Test
    @DisplayName("should not save addresses -> must have at leat one main address")
    void shouldNotSaveAddressMustHaveAtLeastOneMainAddress(){

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
                .mainAddress(false)
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


        //execucao
        Throwable exception = Assertions.catchThrowable(() -> addressService.save(addresses, customer));

        //verificacao
        Mockito.verify(addressRepository, Mockito.never())
                .saveAll(addressesToSave);

        assertThat(exception)
                .isInstanceOf(MustHaveAtLeastOneMainAddres.class)
                .hasMessage("Customer must have at least one main address");

    }

    @Test
    @DisplayName("should save a address")
    void shouldNotSaveAddressesTooManyMainAddresses(){

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
                .mainAddress(true)
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
        Throwable exception = Assertions.catchThrowable(() -> addressService.save(addresses, customer));

        //verificacao
        Mockito.verify(addressRepository, Mockito.never())
                .saveAll(addressesToSave);

        assertThat(exception)
                .isInstanceOf(TooManyMainAddressesException.class)
                .hasMessage("Customer can only have one main address.");
    }

    @Test
    @DisplayName("should not save addresses -> too many addresses")
    void shouldNotSaveAddressToManyAddresses() {

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

        AddressDtoRequest address3 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address4 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();


        AddressDtoRequest address5 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address6 = AddressDtoRequest.builder()
                .state("paraíba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addresses.add(address1);
        addresses.add(address2);
        addresses.add(address3);
        addresses.add(address4);
        addresses.add(address5);
        addresses.add(address6);

        Customer customer = Customer.builder()
                .customerType(CustomerType.FISICA)
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .build();

        //execução
        Throwable exception = Assertions.catchThrowable(() -> addressService.save(addresses, customer));

        //verificação
        assertThat(exception)
                .isInstanceOf(TooManyAddressesException.class)
                .hasMessage("customer can only have 5 addresses.");
    }

    @Test
    @DisplayName("should get addresses by customer")
    void shouldGetAddressesByCustomer(){

        UUID uuid = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .name("ana")
                .document("160.917.000-81")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .build();

        List<Address> addresses = new ArrayList<>();

        Address address1 = Address.builder()
                .state("paraíba")
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        Address address2 = Address.builder()
                .state("paraíba")
                .cep("58.140-000")
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addresses.add(address1);
        addresses.add(address2);

        Mockito.when( addressRepository.findByCustomer( customer ) )
                .thenReturn(addresses);

        //execucao
        List<AddressDtoResponse> addressesByCustomer = addressService.getAddressesByCustomer(customer);

        //verificacao
        Mockito.verify(addressRepository, Mockito.times(1))
                .findByCustomer(customer);
    }

    @Test
    @DisplayName("should delete addresses by customer")
    void shouldDeleteAddressesByCustomer(){

        UUID uuid = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(uuid)
                .customerType(CustomerType.FISICA)
                .name("ana")
                .document("160.917.000-81")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .build();

        List<Address> addresses = new ArrayList<>();

        Address address1 = Address.builder()
                .state("paraíba")
                .id(UUID.randomUUID())
                .cep("58.135-000")
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        addresses.add(address1);


        Mockito.when( addressRepository.findByCustomer( customer ) )
                .thenReturn(addresses);


        doNothing().when(addressRepository).delete(addresses.get(0));

        //execucao
        addressService.deleteAdressesByCustomer(customer);

        //verificação
        Mockito.verify(addressRepository, Mockito.times(1))
                .findByCustomer(customer);

        Mockito.verify(addressRepository, Mockito.times(1))
                .delete(addresses.get(0));

    }


}

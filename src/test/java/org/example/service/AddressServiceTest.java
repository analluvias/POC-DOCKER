package org.example.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.example.domain.entity.Address;
import org.example.domain.entity.Customer;
import org.example.domain.enums.CustomerType;
import org.example.domain.repository.AddressRepository;
import org.example.rest.dto_request.AddressDtoRequest;
import org.example.rest.dto_request.AddressDtoUpdateRequest;
import org.example.rest.dto_response.AddressDtoResponse;
import org.example.rest.dto_response.ViaCepDtoResponse;
import org.example.rest.exception.exceptions.CepShouldHaveStateAndCityException;
import org.example.rest.exception.exceptions.MustHaveAtLeastOneMainAddres;
import org.example.rest.exception.exceptions.NullFieldException;
import org.example.rest.exception.exceptions.ObjectNotFoundException;
import org.example.rest.exception.exceptions.TooManyAddressesException;
import org.example.rest.exception.exceptions.TooManyMainAddressesException;
import org.example.rest.exception.exceptions.ViaCepAccessException;
import org.example.service.impl.AddressServiceImpl;
import org.example.service.impl.ViaCepService;
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
                .document("16091700081")
                .build();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58432800")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address2 = AddressDtoRequest.builder()
                .cep("58432553")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addresses.add(address1);
        addresses.add(address2);

        Address addressToSave1 = Address.builder()
                .state("PB")
                .city("Campina Grande")
                .cep("58432800")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .customer(customer)
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        Address addressToSave2 = Address.builder()
                .state("PB")
                .city("Campina Grande")
                .cep("58432553")
                .customer(customer)
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
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
                .state("PB")
                .city("Campina Grande")
                .cep("58432800")
                .customer(customer)
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        Address addressToReturn2 = Address.builder()
                .id(UUID.randomUUID())
                .state("PB")
                .city("Campina Grande")
                .cep("58432553")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addressesToReturn.add(addressToReturn1);
        addressesToReturn.add(addressToReturn2);


        Mockito.when( addressRepository.saveAll( addressesToSave ) )
                .thenReturn(addressesToReturn);

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
                .document("16091700081")
                .build();

        List<AddressDtoRequest> addresses = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .cep("58432800")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("1233")
                .mainAddress(false)
                .build();

        AddressDtoRequest address2 = AddressDtoRequest.builder()
                .cep("58432553")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        addresses.add(address1);
        addresses.add(address2);

        Address addressToSave1 = Address.builder()
                .state("PB ")
                .city("Campina Grande")
                .cep("58432800")
                .publicArea("Rua Antônio Francisco Alves")
                .customer(customer)
                .district("Malvinas")
                .houseNumber("1233")
                .mainAddress(false)
                .build();

        Address addressToSave2 = Address.builder()
                .state("PB")
                .city("Campina Grande")
                .cep("58432553")
                .customer(customer)
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
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
                .state("PB")
                .city("Campina Grande")
                .cep("58432800")
                .customer(customer)
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        Address addressToReturn2 = Address.builder()
                .id(UUID.randomUUID())
                .state("PB")
                .city("Campina Grande")
                .cep("58432553")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
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
                .cep("58432800")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address2 = AddressDtoRequest.builder()
                .cep("58432553")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        addresses.add(address1);
        addresses.add(address2);

        Address addressToSave1 = Address.builder()
                .state("PB")
                .city("Campina Grande")
                .cep("58432800")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .customer(customer)
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        Address addressToSave2 = Address.builder()
                .state("PB")
                .city("Campina Grande")
                .cep("58432553")
                .customer(customer)
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
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
                .state("PB")
                .city("Campina Grande")
                .cep("58432800")
                .customer(customer)
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        Address addressToReturn2 = Address.builder()
                .id(UUID.randomUUID())
                .state("PB")
                .city("Campina Grande")
                .cep("58432553")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
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
                .cep("58.135-000")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address2 = AddressDtoRequest.builder()
                .cep("58.140-000")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        AddressDtoRequest address3 = AddressDtoRequest.builder()
                .cep("58.135-000")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address4 = AddressDtoRequest.builder()
                .cep("58.140-000")
                .houseNumber("12")
                .mainAddress(false)
                .build();


        AddressDtoRequest address5 = AddressDtoRequest.builder()
                .cep("58.135-000")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        AddressDtoRequest address6 = AddressDtoRequest.builder()
                .cep("58.140-000")
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
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        Address address2 = Address.builder()
                .state("paraíba")
                .cep("58.140-000")
                .district("Areial")
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

    @Test
    @DisplayName("should delete address by id")
    void shouldDeleteAddressById(){

        UUID uuid = UUID.randomUUID();

        Address address = Address.builder()
                .state("paraíba")
                .city("Campina Grande")
                .id(UUID.randomUUID())
                .cep("58432800")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("1233")
                .mainAddress(false)
                .build();

        when(addressRepository.findById(uuid)).thenReturn(Optional.of(address));

        doNothing().when(addressRepository).delete(address);

        //execução
        addressService.deleteById(uuid);

        //verificação
        Mockito.verify(addressRepository, Mockito.times(1))
                .delete(address);

    }

    @Test
    @DisplayName("should throw exception when try to delete nonexistent address by id")
    void shouldThrowErrorWhenTryToDeleteNonExistentAddressById(){

        UUID uuid = UUID.randomUUID();

        Address address = Address.builder()
                .state("paraíba")
                .id(UUID.randomUUID())
                .cep("58.135-000")
                .district("João Pessoa")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        when(addressRepository.findById(uuid)).thenReturn(Optional.of(address));


        //execução
        Throwable exception = Assertions.catchThrowable(() ->addressService.deleteById(uuid));

        //verificação
        assertThat(exception)
                .isInstanceOf(MustHaveAtLeastOneMainAddres.class)
                .hasMessage("Customer must have at least one main address");

    }

    @Test
    @DisplayName("should throw exception when try to get nonexistent address by id")
    void shouldThrowErrorWhenTryToGetNonExistentAddressById(){

        UUID uuid = UUID.randomUUID();

        when(addressRepository.findById(uuid)).thenReturn(Optional.empty());

        //execução
        Throwable exception = Assertions.catchThrowable(() ->addressService.getAddressById(uuid));

        //verificação
        assertThat(exception)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("This address does not exist on DB.");
    }

    @Test
    @DisplayName("should throw exception when send invalid cep to via cep")
    void shouldThrowExceptionWhenSendInvalidCepToViaCep(){

        UUID uuid = UUID.randomUUID();

        AddressDtoRequest request = AddressDtoRequest.builder()
                .cep("5")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("1233")
                .mainAddress(true)
                .build();


        //execução
        Throwable exception = Assertions.catchThrowable(() -> ViaCepService.accessViaCep(request));

        //verificação
        assertThat(exception)
                .isInstanceOf(ViaCepAccessException.class);
    }


    @Test
    @DisplayName("should throw exception when viacep without state")
    void shouldThrowExceptionWhenViaCepWithoutState(){

        UUID uuid = UUID.randomUUID();

        ViaCepDtoResponse viaCep = ViaCepDtoResponse.builder()
                .localidade("exemplo")
                .build();

        AddressDtoRequest request = AddressDtoRequest.builder()
                .cep("88888888")
                .publicArea("Avenida Rio Branco")
                .district("Bela Vista")
                .mainAddress(true)
                .build();

        Customer customer = Customer.builder()
                .email("ana@gmail.com")
                .document("16091700081")
                .phoneNumber("83986868686")
                .name("Joao Ferreira")
                .id( uuid )
                .build();

        //execução
        Throwable exception = Assertions
                .catchThrowable(() -> addressService.buildAddress(viaCep, request, customer));

        //verificação
        assertThat(exception)
                .isInstanceOf(CepShouldHaveStateAndCityException.class);
    }

    @Test
    @DisplayName("should throw exception when viacep without city")
    void shouldThrowExceptionWhenViaCepWithoutCity(){

        UUID uuid = UUID.randomUUID();

        ViaCepDtoResponse viaCep = ViaCepDtoResponse.builder()
                .uf("PB")
                .build();

        AddressDtoRequest request = AddressDtoRequest.builder()
                .cep("88888888")
                .publicArea("Avenida Rio Branco")
                .district("Bela Vista")
                .mainAddress(true)
                .build();

        Customer customer = Customer.builder()
                .email("ana@gmail.com")
                .document("16091700081")
                .phoneNumber("83986868686")
                .name("Joao Ferreira")
                .id( uuid )
                .build();

        //execução
        Throwable exception = Assertions
                .catchThrowable(() -> addressService.buildAddress(viaCep, request, customer));

        //verificação
        assertThat(exception)
                .isInstanceOf(CepShouldHaveStateAndCityException.class);
    }

    @Test
    @DisplayName("should Update main address")
    void shouldUpdateMainAddress() {

        UUID uuidCustomer = UUID.randomUUID();
        Customer customer = Customer.builder()
                .email("ana@gmail.com")
                .document("16091700081")
                .phoneNumber("83986868686")
                .name("Joao Ferreira")
                .id( uuidCustomer )
                .build();

        UUID uuidMainAddress = UUID.randomUUID();
        Address address = Address.builder()
                .id( uuidMainAddress )
                .state( "PB" )
                .cep("58432800")
                .city("Campina Grande")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("25")
                .mainAddress(false)
                .build();

        AddressDtoResponse addressToReturn = AddressDtoResponse.builder()
                .id( uuidMainAddress.toString() )
                .state( "PB" )
                .cep("58432800")
                .city("Campina Grande")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("25")
                .mainAddress(true)
                .build();

        List<Address> addresses = new ArrayList<>();
        Address addressList1 = Address.builder()
                .id( UUID.randomUUID() )
                .state( "PB" )
                .cep("58432800")
                .city("Campina Grande")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("25")
                .mainAddress(true)
                .build();
        addresses.add(addressList1);

        when(addressRepository.findById(uuidMainAddress)).thenReturn(Optional.of(address));

        when(addressRepository.findByCustomer(customer)).thenReturn(addresses);

        List<Address> oldMainAddres = addresses.stream().filter(add -> address.getMainAddress() == true)
                .collect(Collectors.toList());

        oldMainAddres.forEach(add -> add.setMainAddress(false));

        List<Address> savedOldAddresses = new ArrayList<>();
        Address oldAddressSaved = Address.builder()
                .id( UUID.randomUUID() )
                .state( "PB" )
                .cep("58432800")
                .city("Campina Grande")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("25")
                .mainAddress(false)
                .build();
        addresses.add(addressList1);

        when(addressRepository.saveAll(any())).thenReturn( savedOldAddresses );


        Address addressToReturnOnSave = Address.builder()
                .id( uuidMainAddress )
                .state( "PB" )
                .cep("58432800")
                .city("Campina Grande")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("25")
                .mainAddress(true)
                .build();

        when(addressRepository.save(any())).thenReturn(addressToReturnOnSave);

        when(modelMapper.map(any(Address.class), eq(AddressDtoResponse.class)))
                .thenReturn( addressToReturn );

        //execução
        AddressDtoResponse update = addressService.update(uuidMainAddress);

        //verificação
        Mockito.verify(addressRepository, Mockito.times(1))
                .save(any());

        Mockito.verify(addressRepository, Mockito.times(1))
                .saveAll(any());
    }

    @Test
    @DisplayName("should throw exception when address does not have mainAddres field")
    void checkAddressFieldsWithoutMainAddress() {

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder().build();
        addressDtoRequests.add(address1);

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .build();

        //execução
        Throwable exception = Assertions
                .catchThrowable(() ->addressService.save(addressDtoRequests, customer));

        //verificação
        assertThat(exception)
                .isInstanceOf(NullFieldException.class)
                .hasMessage("Address must have mainAddress field.");

    }

    @Test
    @DisplayName("should throw exception when address does not have district field")
    void checkAddressFieldsWithoutDistrict() {

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .mainAddress(true)
                .build();
        addressDtoRequests.add(address1);

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .build();

        //execução
        Throwable exception = Assertions
                .catchThrowable(() ->addressService.save(addressDtoRequests, customer));

        //verificação
        assertThat(exception)
                .isInstanceOf(NullFieldException.class)
                .hasMessage("Address must have district field.");

    }

    @Test
    @DisplayName("should throw exception when address does not have publicArea field")
    void checkAddressFieldsWithoutPublicArea() {

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .mainAddress(true)
                .district("Malvinas")
                .build();
        addressDtoRequests.add(address1);

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .build();

        //execução
        Throwable exception = Assertions
                .catchThrowable(() ->addressService.save(addressDtoRequests, customer));

        //verificação
        assertThat(exception)
                .isInstanceOf(NullFieldException.class)
                .hasMessage("Address must have publicArea field.");

    }

    @Test
    @DisplayName("should throw exception when address does not have Cep field")
    void checkAddressFieldsWithoutCep() {

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .mainAddress(true)
                .district("Malvinas")
                .publicArea("Rua Antônio Francisco Alves")
                .build();
        addressDtoRequests.add(address1);

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .build();

        //execução
        Throwable exception = Assertions
                .catchThrowable(() ->addressService.save(addressDtoRequests, customer));

        //verificação
        assertThat(exception)
                .isInstanceOf(NullFieldException.class)
                .hasMessage("Address must have CEP field.");

    }

    @Test
    @DisplayName("should throw exception when address does not have houseNumber field")
    void checkAddressFieldsWithoutHouseNumber() {

        List<AddressDtoRequest> addressDtoRequests = new ArrayList<>();

        AddressDtoRequest address1 = AddressDtoRequest.builder()
                .mainAddress(true)
                .district("Malvinas")
                .publicArea("Rua Antônio Francisco Alves")
                .cep("58432800")
                .build();
        addressDtoRequests.add(address1);

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .build();

        //execução
        Throwable exception = Assertions
                .catchThrowable(() -> addressService.save(addressDtoRequests, customer));

        //verificação
        assertThat(exception)
                .isInstanceOf(NullFieldException.class)
                .hasMessage("Address must have houseNumber field.");

    }

    @Test
    @DisplayName("should update address")
    void shouldUpdateAddress() {

        UUID uuid = UUID.randomUUID();

        AddressDtoUpdateRequest request = AddressDtoUpdateRequest.builder()
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .cep("58432800")
                .houseNumber("12")
                .build();

        Address oldAddress = Address.builder()
                .id(  uuid  )
                .city("Campina Grande")
                .state("PB")
                .cep("58432553")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDto = AddressDtoRequest.builder()
                .cep( "58432800" )
                .build();

        Address newAddress = Address.builder()
                .id(  uuid  )
                .city("Campina Grande")
                .state("PB")
                .cep("58432800")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        AddressDtoResponse response = AddressDtoResponse.builder()
                .id(  uuid.toString()  )
                .state("PB")
                .city("Campina Grande")
                .cep("58432800")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        when(addressRepository.findById(uuid)).thenReturn(Optional.ofNullable(oldAddress));

        when(addressRepository.save(newAddress)).thenReturn(newAddress);

        when(modelMapper.map(request, AddressDtoRequest.class))
                .thenReturn(  addressDto  );

        when(modelMapper.map(Address.class, AddressDtoResponse.class))
                .thenReturn(response);

        //execução
        AddressDtoResponse updatedAddress = addressService.updateAddress(uuid, request);

        //verificação
        Mockito.verify(addressRepository, Mockito.times(1))
                .save(any());

        Mockito.verify(addressRepository, Mockito.times(1))
                .findById(any());
    }

    @Test
    @DisplayName("should update address cep without state.")
    void shouldUpdateAddressWithoutUf() {

        UUID uuid = UUID.randomUUID();

        AddressDtoUpdateRequest request = AddressDtoUpdateRequest.builder()
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .cep("00000000")
                .houseNumber("12")
                .build();

        Address oldAddress = Address.builder()
                .id(  uuid  )
                .city("Campina Grande")
                .state("PB")
                .cep("58432553")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDto = AddressDtoRequest.builder()
                .cep( "00000000" )
                .build();

        Address newAddress = Address.builder()
                .id(  uuid  )
                .city("Campina Grande")
                .state("PB")
                .cep("58432800")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        AddressDtoResponse response = AddressDtoResponse.builder()
                .id(  uuid.toString()  )
                .state("PB")
                .city("Campina Grande")
                .cep("58432800")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        when(addressRepository.findById(uuid)).thenReturn(Optional.ofNullable(oldAddress));

        when(addressRepository.save(newAddress)).thenReturn(newAddress);

        when(modelMapper.map(request, AddressDtoRequest.class))
                .thenReturn(  addressDto  );

        when(modelMapper.map(Address.class, AddressDtoResponse.class))
                .thenReturn(response);

        //execução
        Throwable exception = Assertions
                .catchThrowable(() ->addressService.updateAddress(uuid, request));

        //verificação
        assertThat(exception)
                .isInstanceOf(CepShouldHaveStateAndCityException.class)
                .hasMessage("Cep should return at least state and city.");

        Mockito.verify(addressRepository, Mockito.never())
                .save(any());

        Mockito.verify(addressRepository, Mockito.times(1))
                .findById(any());
    }


    @Test
    @DisplayName("should update address without cep")
    void shouldUpdateAddressWithoutCep() {

        UUID uuid = UUID.randomUUID();

        AddressDtoUpdateRequest request = AddressDtoUpdateRequest.builder()
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                //.cep("58432800")
                .houseNumber("12")
                .build();

        Address oldAddress = Address.builder()
                .id(  uuid  )
                .city("Campina Grande")
                .state("PB")
                .cep("58432553")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDto = AddressDtoRequest.builder()
                .cep( "58432553" )
                .build();

        Address newAddress = Address.builder()
                .id(  uuid  )
                .city("Campina Grande")
                .state("PB")
                .cep("58432553")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        AddressDtoResponse response = AddressDtoResponse.builder()
                .id(  uuid.toString()  )
                .state("PB")
                .city("Campina Grande")
                .cep("58432553")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        when(addressRepository.findById(uuid)).thenReturn(Optional.ofNullable(oldAddress));

        when(addressRepository.save(newAddress)).thenReturn(newAddress);

        when(modelMapper.map(request, AddressDtoRequest.class))
                .thenReturn(  addressDto  );

        when(modelMapper.map(Address.class, AddressDtoResponse.class))
                .thenReturn(response);

        //execução
        AddressDtoResponse updatedAddress = addressService.updateAddress(uuid, request);

        //verificação
        Mockito.verify(addressRepository, Mockito.times(1))
                .save(any());

        Mockito.verify(addressRepository, Mockito.times(1))
                .findById(any());
    }

    @Test
    @DisplayName("should update address without publicArea")
    void shouldUpdateAddressWithoutPublicArea() {

        UUID uuid = UUID.randomUUID();

        AddressDtoUpdateRequest request = AddressDtoUpdateRequest.builder()
                //.publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .cep("58432800")
                .houseNumber("12")
                .build();

        Address oldAddress = Address.builder()
                .id(  uuid  )
                .city("Campina Grande")
                .state("PB")
                .cep("58432553")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDto = AddressDtoRequest.builder()
                .cep( "58432800" )
                .build();

        Address newAddress = Address.builder()
                .id(  uuid  )
                .city("Campina Grande")
                .state("PB")
                .cep("58432800")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        AddressDtoResponse response = AddressDtoResponse.builder()
                .id(  uuid.toString()  )
                .state("PB")
                .city("Campina Grande")
                .cep("58432800")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        when(addressRepository.findById(uuid)).thenReturn(Optional.ofNullable(oldAddress));

        when(addressRepository.save(newAddress)).thenReturn(newAddress);

        when(modelMapper.map(request, AddressDtoRequest.class))
                .thenReturn(  addressDto  );

        when(modelMapper.map(Address.class, AddressDtoResponse.class))
                .thenReturn(response);

        //execução
        AddressDtoResponse updatedAddress = addressService.updateAddress(uuid, request);

        //verificação
        Mockito.verify(addressRepository, Mockito.times(1))
                .save(any());

        Mockito.verify(addressRepository, Mockito.times(1))
                .findById(any());
    }

    @Test
    @DisplayName("should update address without district")
    void shouldUpdateAddressWithoutDistrict() {

        UUID uuid = UUID.randomUUID();

        AddressDtoUpdateRequest request = AddressDtoUpdateRequest.builder()
                .publicArea("Rua Antônio Francisco Alves")
                //.district("Malvinas")
                .cep("58432800")
                .houseNumber("12")
                .build();

        Address oldAddress = Address.builder()
                .id(  uuid  )
                .city("Campina Grande")
                .state("PB")
                .cep("58432553")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDto = AddressDtoRequest.builder()
                .cep( "58432800" )
                .build();

        Address newAddress = Address.builder()
                .id(  uuid  )
                .city("Campina Grande")
                .state("PB")
                .cep("58432800")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        AddressDtoResponse response = AddressDtoResponse.builder()
                .id(  uuid.toString()  )
                .state("PB")
                .city("Campina Grande")
                .cep("58432800")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        when(addressRepository.findById(uuid)).thenReturn(Optional.ofNullable(oldAddress));

        when(addressRepository.save(newAddress)).thenReturn(newAddress);

        when(modelMapper.map(request, AddressDtoRequest.class))
                .thenReturn(  addressDto  );

        when(modelMapper.map(Address.class, AddressDtoResponse.class))
                .thenReturn(response);

        //execução
        AddressDtoResponse updatedAddress = addressService.updateAddress(uuid, request);

        //verificação
        Mockito.verify(addressRepository, Mockito.times(1))
                .save(any());

        Mockito.verify(addressRepository, Mockito.times(1))
                .findById(any());
    }

    @Test
    @DisplayName("should update address without houseNumber")
    void shouldUpdateAddressWithoutHouseNumber() {

        UUID uuid = UUID.randomUUID();

        AddressDtoUpdateRequest request = AddressDtoUpdateRequest.builder()
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .cep("58432800")
                //.houseNumber("12")
                .build();

        Address oldAddress = Address.builder()
                .id(  uuid  )
                .city("Campina Grande")
                .state("PB")
                .cep("58432553")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDto = AddressDtoRequest.builder()
                .cep( "58432800" )
                .build();

        Address newAddress = Address.builder()
                .id(  uuid  )
                .city("Campina Grande")
                .state("PB")
                .cep("58432800")
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        AddressDtoResponse response = AddressDtoResponse.builder()
                .id(  uuid.toString()  )
                .state("PB")
                .city("Campina Grande")
                .cep("58432800")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        when(addressRepository.findById(uuid)).thenReturn(Optional.ofNullable(oldAddress));

        when(addressRepository.save(newAddress)).thenReturn(newAddress);

        when(modelMapper.map(request, AddressDtoRequest.class))
                .thenReturn(  addressDto  );

        when(modelMapper.map(Address.class, AddressDtoResponse.class))
                .thenReturn(response);

        //execução
        AddressDtoResponse updatedAddress = addressService.updateAddress(uuid, request);

        //verificação
        Mockito.verify(addressRepository, Mockito.times(1))
                .save(any());

        Mockito.verify(addressRepository, Mockito.times(1))
                .findById(any());
    }

    @Test
    @DisplayName("should throw exception when try to update address without city os state by cep")
    void shouldThrowExceptionWhenTryToUpdateAddressWithoutCityOrStateByCep() {

        UUID uuid = UUID.randomUUID();

        AddressDtoUpdateRequest request = AddressDtoUpdateRequest.builder()
                .publicArea("Rua Antônio Francisco Alves")
                .district("Malvinas")
                .cep("88888888")
                .houseNumber("12")
                .build();

        Address oldAddress = Address.builder()
                .id(  uuid  )
                .city("Campina Grande")
                .state("PB")
                .cep("58432553")
                .publicArea("Rua Antônio Gomes Pereira")
                .district("Malvinas")
                .houseNumber("12")
                .mainAddress(true)
                .build();

        AddressDtoRequest addressDto = AddressDtoRequest.builder()
                .cep( "88888888" )
                .build();

        when(addressRepository.findById(uuid)).thenReturn(Optional.ofNullable(oldAddress));

        when(modelMapper.map(request, AddressDtoRequest.class))
                .thenReturn(  addressDto  );

        //execução
        Throwable exception = Assertions
                .catchThrowable(() -> addressService.updateAddress(uuid, request));

        //verificação
        assertThat(exception)
                .isInstanceOf(CepShouldHaveStateAndCityException.class)
                .hasMessage("Cep should return at least state and city.");
    }


}


package org.example.service.impl;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.entity.Address;
import org.example.domain.entity.Customer;
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
import org.example.service.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    protected final AddressRepository addressRepository;

    protected final ModelMapper modelMapper;

    @Transactional
    public List<AddressDtoResponse> save(List<AddressDtoRequest> addressDtoRequest, Customer customer) {

        if (addressDtoRequest.size() > 5)
            throw new TooManyAddressesException();

        AtomicInteger numberOfMainAddress = new AtomicInteger(0);

        List<Address> addresses = addressDtoRequest.stream().map(addressDto -> {

            checkAddressFields(addressDto);

            ViaCepDtoResponse viaCepDtoResponse = ViaCepService.accessViaCep(addressDto);

            Address address = buildAddress(viaCepDtoResponse, addressDto, customer);

            if (Boolean.TRUE.equals(addressDto.getMainAddress())){
                numberOfMainAddress.getAndIncrement();

                if (numberOfMainAddress.get() > 1){
                    throw new TooManyMainAddressesException();
                }

            }

            return address;

        }).collect(Collectors.toList());

        if (numberOfMainAddress.get() == 0)
            throw new MustHaveAtLeastOneMainAddres();

        return addressRepository.saveAll(addresses).stream()
                .map(ad -> modelMapper.map(ad, AddressDtoResponse.class)).collect(Collectors.toList());

    }


    @Override
    @Transactional
    public List<AddressDtoResponse> getAddressesByCustomer(Customer customer) {
        List<Address> addresses = addressRepository.findByCustomer(customer);

        return addresses.stream().map(address -> modelMapper.map(address, AddressDtoResponse.class)).collect(Collectors.toList());
    }

    @Override
    public void deleteAdressesByCustomer(Customer customer) {

        List<Address> addressList = addressRepository.findByCustomer(customer);

        addressList.forEach(addressRepository::delete);

    }

    @Override
    public void deleteById(UUID uuid) {
        Address address = getAddressById(uuid);

        if (address.getMainAddress())
            throw new MustHaveAtLeastOneMainAddres();

        addressRepository.delete(address);
    }

    @Override
    public AddressDtoResponse update(UUID uuid) {
        Address newMainAddress = getAddressById(uuid);

        List<Address> byCustomer = addressRepository.findByCustomer(newMainAddress.getCustomer());
        List<Address> oldMainAddress = byCustomer.stream()
                .filter(address -> address.getMainAddress() == true).collect(Collectors.toList());
        oldMainAddress.forEach(address -> address.setMainAddress(false));

        addressRepository.saveAll(oldMainAddress);

        newMainAddress.setMainAddress(true);
        addressRepository.save(newMainAddress);

        return modelMapper.map(newMainAddress, AddressDtoResponse.class);

    }


    @Override
    public AddressDtoResponse updateAddress(UUID uuid, AddressDtoUpdateRequest request) {

        Address newAddress = getAddressById(uuid);

        ViaCepDtoResponse viaCepDtoResponse;

        if (request.getCep() != null){
            viaCepDtoResponse =
                    ViaCepService.accessViaCep( modelMapper.map(request, AddressDtoRequest.class) );

            if (viaCepDtoResponse.getUf() == null)
                throw new CepShouldHaveStateAndCityException();

            newAddress.setState(viaCepDtoResponse.getUf());
            newAddress.setCity(viaCepDtoResponse.getLocalidade());
        }

        if (request.getPublicArea() != null)
            newAddress.setPublicArea(request.getPublicArea());

        if (request.getDistrict() != null)
            newAddress.setDistrict(request.getDistrict());

        if (request.getHouseNumber() != null)
            newAddress.setHouseNumber(request.getHouseNumber());

        addressRepository.save(newAddress);

        return modelMapper.map(newAddress, AddressDtoResponse.class);
    }

    public Address getAddressById(UUID uuid) {

        Optional<Address> address = addressRepository.findById(uuid);

        if (address.isPresent())
            return address.get();

        throw new ObjectNotFoundException("This address does not exist on DB.");
    }




    private void checkAddressFields(AddressDtoRequest addressDto) {

        if (addressDto.getMainAddress() == null)
            throw new NullFieldException("Address must have mainAddress field.");

        if (addressDto.getDistrict() == null)
            throw new NullFieldException("Address must have district field.");

        if (addressDto.getPublicArea() == null)
            throw new NullFieldException("Address must have publicArea field.");

        if (addressDto.getCep() == null)
            throw new NullFieldException("Address must have CEP field.");

        if (addressDto.getHouseNumber() == null)
            throw new NullFieldException("Address must have houseNumber field.");

    }

    public Address buildAddress(ViaCepDtoResponse viaCep, AddressDtoRequest request, Customer customer) {

        Address buildingAddress = new Address();

        buildingAddress.setCep(request.getCep());

        if (viaCep.getUf() == null)
            throw new CepShouldHaveStateAndCityException();
        else
            buildingAddress.setState( viaCep.getUf() );


        if (viaCep.getLocalidade() == null)
            throw new CepShouldHaveStateAndCityException();
        else
            buildingAddress.setCity(  viaCep.getLocalidade()  );


        buildingAddress.setDistrict(request.getDistrict());
        buildingAddress.setPublicArea( request.getPublicArea() );

        buildingAddress.setHouseNumber(  request.getHouseNumber()  );
        buildingAddress.setMainAddress(  request.getMainAddress()  );
        buildingAddress.setCustomer(customer);

        return buildingAddress;
    }

}

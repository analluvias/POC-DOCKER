package org.example.service.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.domain.entity.Address;
import org.example.domain.entity.Customer;
import org.example.domain.repository.AddressRepository;
import org.example.rest.dto_request.AddressDtoRequest;
import org.example.rest.exception.exceptions.MustHaveAtLeastOneMainAddres;
import org.example.rest.exception.exceptions.TooManyAddresses;
import org.example.rest.exception.exceptions.TooManyMainAddressesException;
import org.example.service.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    protected final AddressRepository addressRepository;

    protected final ModelMapper modelMapper;

    @Transactional
    public void save(List<AddressDtoRequest> addressDtoRequest, Customer customer) {

        if (addressDtoRequest.size() > 5)
            throw new TooManyAddresses();

        AtomicInteger numberOfMainAddress = new AtomicInteger(0);

        List<Address> addresses = addressDtoRequest.stream().map(address -> {
                    Address add = modelMapper.map(address, Address.class);
                    add.setCustomer(customer);

                    if (Boolean.TRUE.equals(address.getMainAddress())){
                        numberOfMainAddress.getAndIncrement();

                        if (numberOfMainAddress.get() > 1){
                            throw new TooManyMainAddressesException();
                        }

                    }

                    return add;
                }).collect(Collectors.toList());

        if (numberOfMainAddress.get() == 0)
            throw new MustHaveAtLeastOneMainAddres();

        addressRepository.saveAll(addresses);

    }
}

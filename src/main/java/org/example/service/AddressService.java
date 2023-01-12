package org.example.service;

import java.util.List;
import java.util.UUID;
import org.example.domain.entity.Customer;
import org.example.rest.dto_request.AddressDtoRequest;
import org.example.rest.dto_request.AddressDtoUpdateRequest;
import org.example.rest.dto_response.AddressDtoResponse;

public interface AddressService {

    List<AddressDtoResponse> save(List<AddressDtoRequest> addressDtoRequest, Customer customer);

    List<AddressDtoResponse> getAddressesByCustomer(Customer customer);

    void deleteAdressesByCustomer(Customer customer);

    void deleteById(UUID uuid);

    AddressDtoResponse update(UUID uuid);

    AddressDtoResponse updateAddress(UUID uuid, AddressDtoUpdateRequest request);
}

package org.example.service;

import java.util.List;
import org.example.domain.entity.Customer;
import org.example.rest.dto_request.AddressDtoRequest;
import org.example.rest.dto_response.AddressDtoResponse;

public interface AddressService {

    public void save(List<AddressDtoRequest> addressDtoRequest, Customer customer);

    List<AddressDtoResponse> getAddressesByCustomer(Customer customer);
}

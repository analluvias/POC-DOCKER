package org.example.service;

import java.util.List;
import org.example.domain.entity.Customer;
import org.example.rest.dto_request.AddressDtoRequest;

public interface AddressService {

    public void save(List<AddressDtoRequest> addressDtoRequest, Customer customer);

}

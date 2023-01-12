package org.example.domain.repository;

import java.util.List;
import java.util.UUID;
import org.example.domain.entity.Address;
import org.example.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByCustomer(Customer customer);

    @Override
    void delete(Address entity);
}

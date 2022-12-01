package org.example.domain.repository;

import java.util.Optional;
import java.util.UUID;
import org.example.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findCustomerByDocument(String document);

}

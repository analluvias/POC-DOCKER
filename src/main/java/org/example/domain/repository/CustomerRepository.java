package org.example.domain.repository;

import java.util.Optional;
import java.util.UUID;
import org.example.domain.entity.Customer;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findCustomerByDocument(String document);

    @Override
    <S extends Customer> Page<S> findAll(Example<S> example, Pageable pageable);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhoneNumber(String phone);
}

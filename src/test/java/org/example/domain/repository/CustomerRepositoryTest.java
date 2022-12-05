package org.example.domain.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;
import org.example.domain.entity.Customer;
import org.example.domain.enums.CustomerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    @DisplayName("should return a customer by his document")
    void returnCustomerByDocument(){
        String document = "160.917.000-81";

        Customer customerToSave = Customer.builder()
                .customerType(CustomerType.FISICA)
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .name("ana")
                .document(document).build();

        entityManager.persist(customerToSave);

        //execucao
        Optional<Customer> customerByDocument = customerRepository.findCustomerByDocument(document);

        assertThat(customerByDocument.get().getDocument()).isEqualTo(customerToSave.getDocument());

    }
}

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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Test
    @DisplayName("should findAll customers by example and Pageble")
    void sholdFindAllCustomersExample(){

        String document = "160.917.000-81";

        Customer customerToSave = Customer.builder()
                .customerType(CustomerType.FISICA)
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .name("ana")
                .document(document).build();

        entityManager.persist(customerToSave);

        Customer customerToSearch = Customer.builder()
                .document(document)
                .build();

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase("name", "email", "document")
                .withStringMatcher(ExampleMatcher.StringMatcher.STARTING);

        Example<Customer> example = Example.of(customerToSearch, matcher);

        //execução
        Page<Customer> customerPage = customerRepository.findAll(example, Pageable.ofSize(20));

        //verificação
        assertThat(customerPage.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("should findAll customers by email")
    void sholdFindCustomerByEmail(){

        String email = "ana@gmail.com";

        Customer customerToSave = Customer.builder()
                .customerType(CustomerType.FISICA)
                .email(email)
                .phoneNumber("83999999999")
                .name("ana")
                .document("160.917.000-81").build();

        entityManager.persist(customerToSave);

        Customer customerToSearch = Customer.builder()
                .email(email)
                .build();

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase("name", "email", "document")
                .withStringMatcher(ExampleMatcher.StringMatcher.STARTING);

        Example<Customer> example = Example.of(customerToSearch, matcher);

        //execução
        Page<Customer> customerPage = customerRepository.findAll(example, Pageable.ofSize(20));

        //verificação
        assertThat(customerPage.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("should findAll customers by email")
    void sholdFindCustomerByPhoneNumber(){

        String phone = "83999999999";

        Customer customerToSave = Customer.builder()
                .customerType(CustomerType.FISICA)
                .email("ana@gmail.com")
                .phoneNumber(phone)
                .name("ana")
                .document("160.917.000-81").build();

        entityManager.persist(customerToSave);

        Customer customerToSearch = Customer.builder()
                .phoneNumber(phone)
                .build();

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase("name", "email", "document")
                .withStringMatcher(ExampleMatcher.StringMatcher.STARTING);

        Example<Customer> example = Example.of(customerToSearch, matcher);

        //execução
        Page<Customer> customerPage = customerRepository.findAll(example, Pageable.ofSize(20));

        //verificação
        assertThat(customerPage.getTotalElements()).isEqualTo(1);
    }


}

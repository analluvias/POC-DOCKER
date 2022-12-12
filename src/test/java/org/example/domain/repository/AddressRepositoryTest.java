package org.example.domain.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.example.domain.entity.Address;
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
public class AddressRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    AddressRepository addressRepository;

    @Test
    @DisplayName("should save a list of addresses")
    void shouldSaveAllAdresses(){

        Customer customer = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(UUID.randomUUID())
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .build();

        Address addressToSave1 = Address.builder()
                .state("paraíba")
                .cep("58.135-000")
                .customer(customer)
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        Address addressToSave2 = Address.builder()
                .state("paraíba")
                .cep("58.140-000")
                .customer(customer)
                .district("Areial")
                .street("Centro")
                .houseNumber("12")
                .mainAddress(false)
                .build();

        Address addressSaved1 = entityManager.persist(addressToSave1);
        Address addressSaved2 = entityManager.persist(addressToSave2);

        assertThat(addressSaved1.getCep()).isEqualTo(addressToSave1.getCep());
        assertThat(addressSaved2.getCep()).isEqualTo(addressToSave2.getCep());

    }

    @Test
    @DisplayName("should delete a addresses")
    void shouldDeleteAAdresses(){

        Customer customer = Customer.builder()
                .customerType(CustomerType.FISICA)
                .id(UUID.randomUUID())
                .name("Ana")
                .email("ana@gmail.com")
                .phoneNumber("83999999999")
                .document("160.917.000-81")
                .build();

        Address addressToSave1 = Address.builder()
                .state("paraíba")
                .cep("58.135-000")
                .customer(customer)
                .district("João Pessoa")
                .street("Rua Joaquim Virgulino da Silva")
                .houseNumber("1233")
                .mainAddress(true)
                .build();

        Address addressSaved1 = entityManager.persist(addressToSave1);

        //execução
        addressRepository.delete(addressSaved1);

        //verificação
        Address deletedAddress = entityManager.find(Address.class, addressSaved1.getId());
        assertThat(deletedAddress).isNull();

    }

}

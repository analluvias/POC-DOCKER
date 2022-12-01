package org.example.domain.entity;

import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.enums.CustomerType;
import org.example.domain.groups.CustomerGroupSequenceProvider;
import org.hibernate.annotations.Type;
import org.hibernate.validator.group.GroupSequenceProvider;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@GroupSequenceProvider(CustomerGroupSequenceProvider.class)
public class Customer {

    @Type(type = "uuid-char")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    //@Email
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @Column(name = "phone", nullable = false)
    @NotEmpty(message = "phone number cannot be empty")
    private String phoneNumber;

    @Column(name = "customerType", nullable = false)
    @NotNull(message = "customer type cannot be empty")
    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    @Column(name = "document", nullable = false, unique = true)
    //@CPF(groups = CPFGroup.class)
    //@CNPJ(groups = CNPJGroup.class)
    @NotEmpty(message = "document cannot be empty")
    private String document;

    @OneToMany(mappedBy = "customer")
    private List<Address> addresses;

}

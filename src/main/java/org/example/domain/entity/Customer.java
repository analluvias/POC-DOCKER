package org.example.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.domain.enums.CustomerType;
import org.example.domain.groups.CNPJGroup;
import org.example.domain.groups.CPFGroup;
import org.example.domain.groups.CustomerGroupSequenceProvider;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;
import org.hibernate.validator.group.GroupSequenceProvider;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@GroupSequenceProvider(CustomerGroupSequenceProvider.class)
public class Customer {

    //lock otimista
    @Version
    private Integer version;

    @Type(type = "uuid-char")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "You need a valid email address.")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @Column(name = "phoneNumber", nullable = false, unique = true)
    @NotBlank(message = "phone number cannot be empty")
    @Length(min = 11, max = 11, message = "phone number must have 11 digits.")
    @Pattern(regexp="\\d+",message="phone number must have only digits.")
    private String phoneNumber;

    @Column(name = "customerType", nullable = false)
    @NotNull(message = "customer type cannot be empty")
    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    @Column(name = "document", nullable = false, unique = true)
    @CPF(groups = CPFGroup.class, message = "CPF has 11 digits. ")
    @CNPJ(groups = CNPJGroup.class, message = "CNPJ has 14 digits.")
    @NotEmpty(message = "document cannot be empty")
    private String document;

    @Column(name = "birthdate")
    @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate birthDate;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Address> addresses;

}

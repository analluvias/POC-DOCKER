package org.example.domain.entity;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import org.hibernate.annotations.Type;
import org.modelmapper.internal.util.Assert;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column
    @NotEmpty(message ="state cannot be empty.")
    private String state;

    @Column
    @NotEmpty(message ="cep cannot be empty.")
    private String cep;

    @Column
    @NotEmpty(message ="district cannot be empty.")
    private String district;

    @Column
    @NotEmpty(message ="street cannot be empty.")
    private String street;

    @Column
    @NotEmpty(message ="house number cannot be empty.")
    private String houseNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column
    @NotNull(message ="You have to specify if this is the main address.")
    private Boolean mainAddress;

}

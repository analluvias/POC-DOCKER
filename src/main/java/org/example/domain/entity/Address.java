package org.example.domain.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.rest.dto_response.ViaCepDtoResponse;
import org.hibernate.annotations.Type;

@Entity
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    //lock otimista
    @Version
    private Integer version;

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column
    @NotEmpty(message ="state cannot be empty.")
    private String state;

    @Column
    @NotEmpty(message ="city cannot be empty.")
    private String city;

    @Column
    @NotEmpty(message ="public area cannot be empty.")
    private String publicArea;

    @Column
    @NotEmpty(message ="cep cannot be empty.")
    private String cep;

    @Column
    @NotEmpty(message ="district cannot be empty.")
    private String district;

    @Column
    @NotEmpty(message ="house number cannot be empty.")
    private String houseNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column
    @NotNull(message ="You have to specify if this is the main address.")
    private Boolean mainAddress;

    public Address mapperViaCepDtoResponseToAddress(ViaCepDtoResponse viaCepDto){

        return Address.builder()
                .cep(viaCepDto.getCep())
                .state(viaCepDto.getUf())
                .city(viaCepDto.getLocalidade())
                .publicArea(viaCepDto.getLogradouro())
                .district(viaCepDto.getBairro())
                .build();

    }

}

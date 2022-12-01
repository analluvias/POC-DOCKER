package org.example.rest.dto_request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDtoRequest {

    @NotEmpty(message ="state cannot be empty.")
    private String state;

    @NotEmpty(message ="cep cannot be empty.")
    private String cep;

    @NotEmpty(message ="district cannot be empty.")
    private String district;

    @NotEmpty(message ="street cannot be empty.")
    private String street;

    @NotEmpty(message ="house number cannot be empty.")
    private String houseNumber;

    //@NotNull(message ="You have to specify if this is the main address.")
    private Boolean mainAddress;
}

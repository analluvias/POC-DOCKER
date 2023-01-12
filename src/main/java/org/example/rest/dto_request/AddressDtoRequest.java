package org.example.rest.dto_request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDtoRequest {

    private String cep;

    private String publicArea;

    private String district;

    private String houseNumber;

    private Boolean mainAddress;
}

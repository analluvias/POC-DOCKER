package org.example.rest.dto_response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDtoResponse {

    private String id;

    private String state;

    private String cep;

    private String district;

    private String street;

    private String houseNumber;

    private Boolean mainAddress;

}

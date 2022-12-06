package org.example.rest.dto_response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDtoResponse {

    private String id;

    private String state;

    private String cep;

    private String district;

    private String street;

    private String houseNumber;

    private Boolean mainAddress;

}

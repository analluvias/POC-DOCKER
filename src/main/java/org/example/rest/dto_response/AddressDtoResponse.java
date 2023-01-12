package org.example.rest.dto_response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDtoResponse implements Serializable{

    private String id;

    private String state;

    private String city;

    private String publicArea;

    private String cep;

    private String district;

    private String houseNumber;

    private Boolean mainAddress;

}

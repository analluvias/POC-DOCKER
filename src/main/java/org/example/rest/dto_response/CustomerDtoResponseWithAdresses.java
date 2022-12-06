package org.example.rest.dto_response;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.enums.CustomerType;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDtoResponseWithAdresses {

    private UUID id;

    private String name;

    private String email;

    private String phoneNumber;

    private CustomerType customerType;

    private String document;

    List<AddressDtoResponse> addresses;

}

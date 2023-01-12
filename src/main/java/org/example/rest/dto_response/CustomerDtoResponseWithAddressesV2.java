package org.example.rest.dto_response;

import java.io.Serializable;
import java.time.LocalDate;
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
public class CustomerDtoResponseWithAddressesV2 implements Serializable {

    private UUID id;

    private String name;

    private LocalDate birthDate;

    private String email;

    private String phoneNumber;

    private CustomerType customerType;

    private String document;

    List<AddressDtoResponse> addresses;

}

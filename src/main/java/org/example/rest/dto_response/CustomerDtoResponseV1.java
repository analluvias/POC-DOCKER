package org.example.rest.dto_response;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.enums.CustomerType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDtoResponseV1 implements Serializable {

    private UUID id;

    private String name;

    private String email;

    private String phoneNumber;

    private CustomerType customerType;

    private String document;

}

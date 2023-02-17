package org.example.rest.dto_request;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.enums.CustomerType;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDtoRequestV1 {

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @NotEmpty(message = "Phone number cannot be empty")
    private String phoneNumber;

    @NotNull(message = "customer type cannot be empty")
    private String customerType;

    @NotEmpty(message = "document cannot be empty")
    private String document;

    @NotEmpty(message = "client must have at least one adress.")
    private List<AddressDtoRequest> addresses;
}

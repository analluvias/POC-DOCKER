package org.example.rest.dto_request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.enums.CustomerType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDtoRequestV2 {

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "birth date cannot be empty")
    @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate birthDate;

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

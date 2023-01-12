package org.example.rest.dto_response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ViaCepDtoResponse {

    String cep;

    String logradouro;

    String localidade;
    String bairro;

    String uf;
}

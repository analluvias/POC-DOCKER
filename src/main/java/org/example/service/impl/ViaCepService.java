package org.example.service.impl;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import org.example.rest.dto_request.AddressDtoRequest;
import org.example.rest.dto_response.ViaCepDtoResponse;
import org.example.rest.exception.exceptions.ViaCepAccessException;
import org.springframework.stereotype.Service;

@Service
public class ViaCepService {

    public static ViaCepDtoResponse accessViaCep(AddressDtoRequest addressDto) {

        try {

            URL url = new URL("https://viacep.com.br/ws/"+ addressDto.getCep()+"/json/");
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            String cep = "";
            StringBuilder jsonCep = new StringBuilder();
            while ((cep = br.readLine()) != null){
                jsonCep.append(cep);
            }

            //log.info(jsonCep.toString());

            return new Gson()
                    .fromJson(jsonCep.toString(), ViaCepDtoResponse.class);

        } catch (Exception e) {

            throw new ViaCepAccessException(e);

        }

    }

}

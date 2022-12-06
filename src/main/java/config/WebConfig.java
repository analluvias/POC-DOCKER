package config;

import org.example.domain.enums.CustomerType;
import org.example.rest.exception.exceptions.InvalidCustomerTypeException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry){
        registry.addConverter(new Converter<String, CustomerType>() {

            @Override
            public CustomerType convert(String source) {
                try{
                    return CustomerType.valueOf(source.toUpperCase());
                }catch (IllegalArgumentException ex){
                    throw new InvalidCustomerTypeException();
                }
            }

            @Override
            public <U> Converter<String, U> andThen(Converter<? super CustomerType, ? extends U> after) {
                return Converter.super.andThen(after);
            }
        });
    }
}

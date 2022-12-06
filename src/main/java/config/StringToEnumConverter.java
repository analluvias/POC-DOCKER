//package config;
//
//import com.fasterxml.jackson.databind.JavaType;
//import com.fasterxml.jackson.databind.type.TypeFactory;
//import com.fasterxml.jackson.databind.util.Converter;
//import org.example.domain.enums.CustomerType;
//import org.example.rest.exception.exceptions.InvalidCustomerTypeException;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class StringToEnumConverter implements Converter<String, CustomerType> {
//
//    // configuração para converter a string que entrar no
//    // request param para o enumCustomerTYPE
//    @Override
//    public CustomerType convert(String s) {
//
//        try{
//            return CustomerType.valueOf(s.toUpperCase());
//        }catch (IllegalArgumentException ex){
//            throw new InvalidCustomerTypeException();
//        }
//
//    }
//
//    @Override
//    public JavaType getInputType(TypeFactory typeFactory) {
//        return null;
//    }
//
//    @Override
//    public JavaType getOutputType(TypeFactory typeFactory) {
//        return null;
//    }
//}

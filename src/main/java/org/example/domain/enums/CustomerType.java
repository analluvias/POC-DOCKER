package org.example.domain.enums;

import org.example.domain.groups.CNPJGroup;
import org.example.domain.groups.CPFGroup;

public enum CustomerType {
    FISICA("Fisica", "CPF", "000.000.000-00", CPFGroup.class),
    JURIDICA("Juridica", "CNPJ", "00.000.000/0000-00", CNPJGroup.class);

    private final String description;
    private final String document;
    private final String identifier;
    private final Class<?> group;


    CustomerType(String description, String document, String identifier, Class<?> group) {
        this.description = description;
        this.document = document;
        this.identifier = identifier;
        this.group = group;
    }

    public String getDescription(){
        return description;
    }

    public String getDocument(){
        return document;
    }

    public String getIdentifier(){
        return identifier;
    }

    public Class<?> getGroup(){
        return group;
    }


}

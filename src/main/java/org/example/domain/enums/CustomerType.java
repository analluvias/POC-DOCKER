package org.example.domain.enums;

import org.example.domain.groups.CNPJGroup;
import org.example.domain.groups.CPFGroup;

public enum CustomerType {
    FISICA("Fisica", "CPF", "00000000000", CPFGroup.class),
    JURIDICA("Juridica", "CNPJ", "00000000000000", CNPJGroup.class);

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

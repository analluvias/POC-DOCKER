package org.example.domain.groups;

import org.example.domain.entity.Customer;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import java.util.ArrayList;
import java.util.List;

public class CustomerGroupSequenceProvider implements DefaultGroupSequenceProvider<Customer> {

    @Override
    public List<Class<?>> getValidationGroups(Customer customer) {
        List<Class<?>> groups = new ArrayList<>();
        groups.add(Customer.class);

        if (isCustomerNotNull(customer)){
            groups.add(customer.getCustomerType().getGroup());
        }

        return groups;

    }

    protected boolean isCustomerNotNull(Customer customer){
        return customer != null && customer.getCustomerType() != null;
    }

}

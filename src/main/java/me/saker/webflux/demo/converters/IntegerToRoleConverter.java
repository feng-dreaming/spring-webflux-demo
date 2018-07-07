package me.saker.webflux.demo.converters;

import me.saker.webflux.demo.domain.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class IntegerToRoleConverter implements Converter<Integer, Role> {

    @Override
    public Role convert(Integer source) {
        for (Role role : Role.values()) {
            if (role.getValue() == source)
                return role;
        }
        return null;
    }
}
package com.venuehub.authservice.dto;

import com.venuehub.authservice.model.Role;
import com.venuehub.authservice.model.User;

import java.util.stream.Collectors;

public class Mapper {
    public UserDto modelToDto(User userModel) {
        return new UserDto(userModel.getUsername(),userModel.getAuthorities().stream().map(Role::getAuthority).collect(Collectors.toList()));
    }
}


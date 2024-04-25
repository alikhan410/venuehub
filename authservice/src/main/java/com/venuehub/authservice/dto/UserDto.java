package com.venuehub.authservice.dto;


import java.util.List;


public record UserDto(String username, List<String> roles) {

}

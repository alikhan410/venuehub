package com.venuehub.authservice.dto;


import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Cacheable("login:currentUser")
public record CurrentUserDto(String username, List<String> roles, String loggedInAs) {

}

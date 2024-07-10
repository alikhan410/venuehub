package com.venuehub.authservice.repository;

import com.venuehub.authservice.dto.RoleDto;
import com.venuehub.authservice.model.Role;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByAuthority(String authority);

    @Cacheable(value = "role:authority", key = "#authority")
    default RoleDto loadByAuthority(String authority) {
        Role role = findByAuthority(authority).orElseThrow(NoSuchElementException::new);
        return new RoleDto(role.getAuthority());
    }
}
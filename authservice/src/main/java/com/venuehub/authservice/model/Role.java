package com.venuehub.authservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "authority")
    private String authority;

    public Role(String authority){
        this.authority = authority;
    }

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, mappedBy = "authorities")
    @JsonBackReference
    private Set<User> users = new HashSet<>();

    @Override
    public String getAuthority() {
        return authority;
    }
}

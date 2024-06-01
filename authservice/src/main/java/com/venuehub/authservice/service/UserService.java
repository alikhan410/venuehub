package com.venuehub.authservice.service;

import com.venuehub.authservice.model.User;
import com.venuehub.authservice.repository.UserRepository;
import com.venuehub.commons.exception.NoSuchUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(NoSuchUserException::new);
    }

    @Transactional
    public User save(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(NoSuchUserException::new);
    }
}
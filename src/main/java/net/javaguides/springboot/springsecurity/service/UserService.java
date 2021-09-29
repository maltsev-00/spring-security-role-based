package net.javaguides.springboot.springsecurity.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import net.javaguides.springboot.springsecurity.model.User;
import net.javaguides.springboot.springsecurity.web.dto.UserRegistrationDto;

public interface UserService extends UserDetailsService {
/**
 * Comment for finding Email By findByEmail
 */
    User findByEmail(String email);

    User save(UserRegistrationDto registration);
}

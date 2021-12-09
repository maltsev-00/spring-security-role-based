package net.javaguides.springboot.springsecurity.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import net.javaguides.springboot.springsecurity.model.entity.User;
import net.javaguides.springboot.springsecurity.model.dto.UserRegistrationDto;

public interface UserService extends UserDetailsService {

    User findByEmail(String email);

    User save(UserRegistrationDto registration);
}

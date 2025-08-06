package com.devansh.service;

import com.devansh.config.JwtService;
import com.devansh.exception.UserException;
import com.devansh.model.User;
import com.devansh.repo.UserRepository;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public User findByJwtToken(String token) throws UserException {
        token = token.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new BadCredentialsException("User not found with email: " + email));
        return user;
    }

}
































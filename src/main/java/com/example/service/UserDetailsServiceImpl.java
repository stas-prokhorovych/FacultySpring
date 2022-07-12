package com.example.service;

import com.example.exception.UserNotFoundException;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        if(userRepository.findUserByLogin(login).isEmpty()) {
            throw new UsernameNotFoundException("User not authorized.");
        }
        return userRepository.findUserByLogin(login).orElseThrow(UserNotFoundException::new);
    }
}

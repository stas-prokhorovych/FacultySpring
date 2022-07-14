package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userDetailsService")
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = Logger.getLogger(JournalService.class);

    private final UserRepository userRepository;

    /**
     * @param login entered login
     * @return UserDetails
     * @throws UsernameNotFoundException if no user found
     */
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByLogin(login);
        if(user.isEmpty()) {
            log.info("Authorization fail");
            throw new UsernameNotFoundException("User not authorized.");
        }
        return user.get();
    }
}

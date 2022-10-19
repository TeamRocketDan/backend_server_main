package com.rocket.config.oauth2.service;

import com.rocket.config.oauth2.entity.UserPrincipal;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findByUuid(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Can not find username."));

            return UserPrincipal.create(user);
        }
}

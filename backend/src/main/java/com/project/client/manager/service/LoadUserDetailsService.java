package com.project.client.manager.service;

import com.project.client.manager.model.User;
import com.project.client.manager.repository.UserRepository;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoadUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public LoadUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found : " + username));

    if (user.getRole() == null) {
      throw new UsernameNotFoundException("User role is missing: " + username);
    }

    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
        .disabled(!user.isEnabled())
        .build();
  }
}

package com.project.client.manager.service;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public interface UserDetails {

  Collection<? extends GrantedAuthority> getAuthorities();

  String getPassword();

  String getUserNAme();

  boolean isAccountNonExpired();

  boolean isAccountNonLocked();

  boolean isCredentialsNonExpired();

  boolean isEnabled();
}

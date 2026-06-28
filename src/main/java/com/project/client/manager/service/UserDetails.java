package com.project.client.manager.service;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface UserDetails {

    Collection<? extends GrantedAuthority> getAuthorities();

    String getPassword();

    String getUserNAme();

    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isCredentialsNonExpired();

    boolean isEnabled();
}

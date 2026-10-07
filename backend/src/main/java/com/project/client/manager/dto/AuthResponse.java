package com.project.client.manager.dto;

import com.project.client.manager.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
  private String token;
  private String type = "Bearer";
  private String username;
  private String email;
  private Role role;
}

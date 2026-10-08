package com.project.client.manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  @NotBlank(message = "Client name is required")
  @Size(min = 2, max = 100, message = "Client name must be between 2 and 100 characters")
  private String name;

  @ManyToMany(mappedBy = "clients")
  @Builder.Default
  @EqualsAndHashCode.Exclude
  @JsonIgnore
  private Set<User> users = new HashSet<>();

  @Column(nullable = false, unique = true)
  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

  @jakarta.validation.constraints.Pattern(
      regexp = "^$|^\\+?[1-9]\\d{1,14}$",
      message = "Phone number must be a valid E.164 format (optional)")
  private String phone;

  private String address;

  private String taxId;

  private Timestamp createdAt;

  private Timestamp updatedAt;

  @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  @EqualsAndHashCode.Exclude
  private List<Invoice> invoices;
}

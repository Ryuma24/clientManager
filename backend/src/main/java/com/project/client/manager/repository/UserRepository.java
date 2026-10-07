package com.project.client.manager.repository;

import com.project.client.manager.model.Client;
import com.project.client.manager.model.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  List<Client> findClientsByUsername(String username);

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);
}

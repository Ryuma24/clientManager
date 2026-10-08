package com.project.client.manager.repository;

import com.project.client.manager.model.Client;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

  List<Client> findByUsers_Username(String username);

  Optional<Client> findByEmailIgnoreCase(String email);

  Optional<Client> findByIdAndUsers_Username(Long clientId, String username);
}

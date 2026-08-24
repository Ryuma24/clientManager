package com.project.client.manager.repository;

import com.project.client.manager.model.Client;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

  List<Client> findByUserId(Long userId);

  Optional<Client> findById(Long id);

  Optional<Client> findByIdAndUserId(Long id, Long userId);

  List<Client> findByUserUsername(String username);

  Optional<Client> findByEmail(String email);

  Optional<Client> findByIdAndUserUsername(Long clientId, String Username);
}

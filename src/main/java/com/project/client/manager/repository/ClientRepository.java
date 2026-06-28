package com.project.client.manager.repository;

import com.project.client.manager.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByUserId(Long userId);

    Optional<Client> findByIdAndUserId(Long id , Long userId);

}

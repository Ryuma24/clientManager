package com.project.client.manager.repository;

import com.project.client.manager.model.Client;
import com.project.client.manager.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

}

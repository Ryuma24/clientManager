package com.project.client.manager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;

@Data
@Entity
public class Payment {
    @Id
    @GeneratedValue
    private Long id;

    private Long invoiceId;

    private Double amount;

    private PaymentStatus status;

    private String transactionId;

    private String gateway;

    private Timestamp paymentDoneAt;
}

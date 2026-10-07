package com.project.client.manager.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
  @Id @GeneratedValue private Long id;

  @ManyToOne
  @JoinColumn(name = "invoice_id")
  private Invoice invoice;

  private BigDecimal amount;

  private PaymentStatus status;

  private String transactionId;

  private String gateway;

  private Timestamp paymentDoneAt;

  private String razorpayOrderId;

  private String razorpayPaymentId;
}

package com.project.client.manager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "invoices")
@Builder
@Entity
public class Invoice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  @Transient
  private Long clientId;

  @Column(unique = true, nullable = false)
  @NotBlank(message = "Invoice number is required")
  private String invoiceNumber;

  @Column(nullable = false)
  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  private Double subTotal;

  @Column(nullable = false)
  @Pattern(regexp = "DRAFT|SENT|PENDING|PAID|OVERDUE", message = "Invalid status")
  private String status;

  @Column(nullable = false)
  @NotNull(message = "Issue date is required")
  @PastOrPresent(message = "Issue date cannot be in the future")
  private LocalDate issueDate;

  @Column(nullable = false)
  @NotNull(message = "Due date is required")
  @FutureOrPresent(message = "Due date must be in the future")
  private LocalDate dueDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", nullable = false)
  @NotNull(message = "Client is required")
  @JsonBackReference
  private Client client;

  private PaymentAmountStatus amountStatus;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "invoice_items", joinColumns = @JoinColumn(name = "invoice_id"))
  private List<InvoiceItem> invoiceItemList;

  @ElementCollection private List<Payment> paymentsList;

  private BigDecimal taxAmount;

  private BigDecimal amountPaid;

  private BigDecimal totalAmount;

  private Timestamp createdAt;

  private Timestamp updatedAt;
}

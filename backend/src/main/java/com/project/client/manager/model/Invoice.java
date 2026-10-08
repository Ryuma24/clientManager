package com.project.client.manager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String invoiceNumber;

  @Column(nullable = false)
  private BigDecimal subTotal;

  @Column(nullable = false)
  private String status;

  @Column(nullable = false)
  private LocalDate issueDate;

  @Column(nullable = false)
  private LocalDate dueDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", nullable = false)
  @JsonBackReference
  private Client client;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_user_id", nullable = false)
  @JsonIgnore
  private User createdBy;

  @Enumerated(EnumType.STRING)
  private PaymentAmountStatus amountStatus;

  @ElementCollection
  @CollectionTable(name = "invoice_items", joinColumns = @JoinColumn(name = "invoice_id"))
  private List<InvoiceItem> invoiceItemList;
  
  @OneToMany(
          mappedBy = "invoice",
          cascade = CascadeType.ALL,
          orphanRemoval = true
  )
  private List<Payment> paymentsList;

  private BigDecimal taxAmount;

  private BigDecimal amountPaid;

  private BigDecimal totalAmount;

  @Column(nullable = false, updatable = false)
  private Timestamp createdAt;

  private Timestamp updatedAt;
}

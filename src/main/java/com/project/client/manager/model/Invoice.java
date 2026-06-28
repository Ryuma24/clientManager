package com.project.client.manager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

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

    @ElementCollection
    private List<InvoiceItem> invoiceItemList;

    private Double taxAmount;

    private Double amountPaid;

    private Double balanceAmount;

    private Timestamp createdAt;

    private Timestamp updatedAt;


}

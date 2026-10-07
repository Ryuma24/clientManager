package com.project.client.manager.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {

  @NotNull(message = "Client ID is required")
  private Long clientId;

  @NotBlank(message = "Invoice number is required")
  private String invoiceNumber;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  private Double amount;

  @NotNull(message = "Issue date is required")
  @PastOrPresent(message = "Issue date cannot be in the future")
  private LocalDate issueDate;

  @NotNull(message = "Due date is required")
  @Future(message = "Due date must be in the future")
  private LocalDate dueDate;

  private String status = "DRAFT";
}

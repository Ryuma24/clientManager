package com.project.client.manager.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
  private Long id;
  private String invoiceNumber;
  private Double amount;
  private String status;
  private LocalDate issueDate;
  private LocalDate dueDate;
  private Long clientId;
  private String clientName;
}

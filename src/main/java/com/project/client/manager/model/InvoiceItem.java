package com.project.client.manager.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class InvoiceItem {
  private String itemName;

  private Double amount;

  private Integer quantity;

  private Double unitPrice;
}

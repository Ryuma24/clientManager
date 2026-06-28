package com.project.client.manager.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class InvoiceItem {
    @Id
    @GeneratedValue
    private Long id;

    private Long invoiceId;

    private String itemName;

    private Double amount;

    private Integer quantity;

    private Double unitPrice;

}

package com.project.client.manager.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentRequest {

  private Long invoiceId;

  private BigDecimal amount;
}

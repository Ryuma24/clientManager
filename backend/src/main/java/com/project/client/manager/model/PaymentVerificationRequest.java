package com.project.client.manager.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentVerificationRequest {
  private String razorpayOrderId;
  private String razorpayPaymentId;
  private String razorpaySignature;
}

package com.project.client.manager.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.client.manager.model.Invoice;
import com.project.client.manager.model.Payment;
import com.project.client.manager.model.PaymentAmountStatus;
import com.project.client.manager.model.PaymentStatus;
import com.project.client.manager.model.PaymentVerificationRequest;
import com.project.client.manager.repository.InvoiceRepository;
import com.project.client.manager.repository.PaymentRepository;
import com.project.client.manager.repository.UserRepository;
import com.razorpay.RazorpayClient;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Mock private InvoiceRepository invoiceRepository;
  @Mock private PaymentRepository paymentRepository;
  @Mock private UserRepository userRepository;
  @Mock private RazorpayClient razorpayClient;

  @InjectMocks private PaymentService paymentService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(paymentService, "razorpayKeySecret", "test-secret");
  }

  @Test
  void verifyPayment_shouldIgnoreAlreadyProcessedOrder() throws Exception {
    Invoice invoice = new Invoice();
    invoice.setId(10L);
    invoice.setSubTotal(BigDecimal.valueOf(500));
    invoice.setAmountPaid(BigDecimal.valueOf(500));
    invoice.setTotalAmount(BigDecimal.valueOf(500));
    invoice.setAmountStatus(PaymentAmountStatus.FULL);
    invoice.setStatus("PAID");

    Payment completedPayment =
        Payment.builder()
            .id(1L)
            .invoice(invoice)
            .amount(BigDecimal.valueOf(500))
            .status(PaymentStatus.SUCCESSFUL)
            .razorpayOrderId("order_123")
            .razorpayPaymentId("pay_123")
            .build();

    when(paymentRepository.findByRazorpayOrderId("order_123"))
        .thenReturn(Optional.of(completedPayment));

    String signature = createSignature("order_123|pay_123", "test-secret");

    PaymentVerificationRequest request =
        PaymentVerificationRequest.builder()
            .razorpayOrderId("order_123")
            .razorpayPaymentId("pay_123")
            .razorpaySignature(signature)
            .build();

    assertFalse(paymentService.verifyPayment(request));
    verify(invoiceRepository, never()).save(any());
    verify(paymentRepository, never()).save(any());
  }

  private String createSignature(String payload, String secret)
      throws NoSuchAlgorithmException, InvalidKeyException {
    Mac sha256Hmac = Mac.getInstance("HmacSHA256");
    SecretKeySpec secretKeySpec =
        new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    sha256Hmac.init(secretKeySpec);
    return Hex.encodeHexString(sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
  }
}

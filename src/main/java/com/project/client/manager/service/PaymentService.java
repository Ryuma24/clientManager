package com.project.client.manager.service;

import com.project.client.manager.model.*;
import com.project.client.manager.repository.InvoiceRepository;
import com.project.client.manager.repository.PaymentRepository;
import com.project.client.manager.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final InvoiceRepository invoiceRepository;
  private final PaymentRepository paymentRepository;
  private final UserRepository userRepository;
  private final RazorpayClient razorpayClient;

  @Value("${razorpay.key.secret}")
  private String razorpayKeySecret;

  public Payment createPayment(PaymentRequest paymentRequest, String username) {

    if (paymentRequest == null
        || paymentRequest.getInvoiceId() == null
        || paymentRequest.getAmount() == null
        || paymentRequest.getAmount().signum() <= 0) {
      throw new RuntimeException("Invalid payment request");
    }

    Invoice invoice =
        invoiceRepository
            .findByIdAndClient_Email(paymentRequest.getInvoiceId(), userEmail(username))
            .orElseThrow(() -> new RuntimeException("Invoice does not exist"));

    BigDecimal total =
        invoice.getTotalAmount() == null ? BigDecimal.valueOf(invoice.getSubTotal()) : invoice.getTotalAmount();
    BigDecimal currentPaid = invoice.getAmountPaid() == null ? BigDecimal.ZERO : invoice.getAmountPaid();
    BigDecimal remaining = total.subtract(currentPaid);

    if (paymentRequest.getAmount().compareTo(remaining) > 0) {
      throw new RuntimeException("Payment amount exceeds remaining invoice balance");
    }

    try {
      long amountInPaise = paymentRequest.getAmount().multiply(BigDecimal.valueOf(100)).longValueExact();

      JSONObject orderRequest = new JSONObject();
      orderRequest.put("amount", amountInPaise);
      orderRequest.put("currency", "INR");
      orderRequest.put("receipt", "invoice_" + invoice.getId());

      Order razorpayOrder = razorpayClient.orders.create(orderRequest);

      Payment payment =
          Payment.builder()
              .amount(paymentRequest.getAmount())
              .invoice(invoice)
              .razorpayOrderId(razorpayOrder.get("id"))
              .status(PaymentStatus.PENDING)
              .build();

      return paymentRepository.save(payment);
    } catch (Exception e) {
      throw new RuntimeException("Unable to create Razorpay order");
    }
  }

  @Transactional
  public Boolean verifyPayment(PaymentVerificationRequest request)
      throws InvalidKeyException, NoSuchAlgorithmException {

    if (request == null
        || request.getRazorpayOrderId() == null
        || request.getRazorpayPaymentId() == null
        || request.getRazorpaySignature() == null) {
      return false;
    }

    Payment payment =
        paymentRepository
            .findByRazorpayOrderId(request.getRazorpayOrderId())
            .orElseThrow(() -> new RuntimeException("Payment record not found"));

    if (payment.getStatus() == PaymentStatus.SUCCESSFUL) {
      return false;
    }

    String signature = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();

    Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
    SecretKeySpec secretKeySpec =
        new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    sha256_HMAC.init(secretKeySpec);

    String generatedSignature =
        Hex.encodeHexString(sha256_HMAC.doFinal(signature.getBytes(StandardCharsets.UTF_8)));

    boolean isValidSignature = generatedSignature.equals(request.getRazorpaySignature());
    if (!isValidSignature) {
      return false;
    }

    Invoice invoice = payment.getInvoice();
    if (invoice == null) {
      return false;
    }

    BigDecimal paidAmount = payment.getAmount() == null ? BigDecimal.ZERO : payment.getAmount();
    BigDecimal currentPaid = invoice.getAmountPaid() == null ? BigDecimal.ZERO : invoice.getAmountPaid();
    BigDecimal newTotalPaid = currentPaid.add(paidAmount);

    invoice.setAmountPaid(newTotalPaid);
    invoice.setTotalAmount(
        invoice.getTotalAmount() == null ? BigDecimal.valueOf(invoice.getSubTotal()) : invoice.getTotalAmount());

    if (newTotalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
      invoice.setAmountStatus(PaymentAmountStatus.FULL);
      invoice.setStatus("PAID");
    } else {
      invoice.setAmountStatus(PaymentAmountStatus.PARTIAL);
      invoice.setStatus("PENDING");
    }

    payment.setStatus(PaymentStatus.SUCCESSFUL);
    payment.setTransactionId(request.getRazorpayPaymentId());
    payment.setGateway("RAZORPAY");
    payment.setPaymentDoneAt(new Timestamp(System.currentTimeMillis()));

    invoiceRepository.save(invoice);
    paymentRepository.save(payment);
    return true;
  }

  @Transactional
  public void doPayment(Long paymentId, PaymentRequest paymentRequest, String username) {

    if (paymentRequest == null || paymentRequest.getInvoiceId() == null || paymentRequest.getAmount() == null
        || paymentRequest.getAmount().signum() <= 0) {
      throw new RuntimeException("Invalid payment request");
    }

    Invoice invoice =
        invoiceRepository
            .findByIdAndClient_Email(paymentRequest.getInvoiceId(), userEmail(username))
            .orElseThrow(() -> new RuntimeException("Invoice does not exist"));

    Payment payment =
        paymentRepository
            .findByIdAndInvoiceId(paymentId, paymentRequest.getInvoiceId())
            .orElseThrow(() -> new RuntimeException("Payment Does not exist!"));

    if (payment.getStatus() == PaymentStatus.SUCCESSFUL) {
      return;
    }

    BigDecimal total =
        invoice.getTotalAmount() == null ? BigDecimal.valueOf(invoice.getSubTotal()) : invoice.getTotalAmount();
    BigDecimal currentPaid = invoice.getAmountPaid() == null ? BigDecimal.ZERO : invoice.getAmountPaid();
    BigDecimal remaining = total.subtract(currentPaid);

    if (paymentRequest.getAmount().compareTo(remaining) > 0) {
      throw new RuntimeException("Payment amount exceeds remaining invoice balance");
    }

    invoice.setAmountPaid(currentPaid.add(paymentRequest.getAmount()));

    if (invoice.getAmountPaid().compareTo(total) >= 0) {
      invoice.setAmountStatus(PaymentAmountStatus.FULL);
      invoice.setStatus("PAID");
    } else {
      invoice.setAmountStatus(PaymentAmountStatus.PARTIAL);
      invoice.setStatus("PENDING");
    }

    payment.setStatus(PaymentStatus.SUCCESSFUL);
    payment.setGateway("RAZORPAY");
    payment.setPaymentDoneAt(new Timestamp(System.currentTimeMillis()));

    invoiceRepository.save(invoice);
    paymentRepository.save(payment);
  }

  private String userEmail(String username) {
    return userRepository
        .findByUsername(username)
        .map(user -> user.getEmail())
        .orElseThrow(() -> new RuntimeException("User does not exist"));
  }
}

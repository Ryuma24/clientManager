package com.project.client.manager.controller;

import com.project.client.manager.model.Payment;
import com.project.client.manager.model.PaymentRequest;
import com.project.client.manager.model.PaymentVerificationRequest;
import com.project.client.manager.service.PaymentService;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentsController {

  private final PaymentService paymentService;

  @PostMapping("/create")
  public ResponseEntity<Payment> createPayment(
      @RequestBody PaymentRequest paymentRequest, Authentication authentication) {
    Payment payment = paymentService.createPayment(paymentRequest, authentication.getName());

    return ResponseEntity.status(HttpStatus.CREATED).body(payment);
  }

  @PostMapping("/verify")
  public ResponseEntity<String> verifyPayment(@RequestBody PaymentVerificationRequest request)
      throws NoSuchAlgorithmException, InvalidKeyException {

    if (paymentService.verifyPayment(request)) {
      return ResponseEntity.ok("Payment verified successfully");
    }

    return ResponseEntity.badRequest().body("Payment verification failed");
  }
}

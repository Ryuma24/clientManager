package com.project.client.manager.repository;

import com.project.client.manager.model.Payment;
import com.project.client.manager.model.PaymentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

  List<Payment> findByInvoiceId(Long invoiceId);

  Optional<Payment> findByIdAndInvoiceId(Long id, Long invoiceId);

  Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

  List<Payment> findByStatus(PaymentStatus status);
}

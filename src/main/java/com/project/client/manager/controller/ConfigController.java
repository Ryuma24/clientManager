package com.project.client.manager.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ConfigController {

  @Value("${razorpay.key.id:#{null}}")
  private String razorpayKeyIdProp;

  @GetMapping("/config")
  public ResponseEntity<?> getConfig() {
    String key = razorpayKeyIdProp;
    if (key == null || key.isBlank()) {
      key = System.getenv("RAZORPAY_KEY_ID");
    }
    if (key == null || key.isBlank()) {
      return ResponseEntity.status(500).body(Map.of("message", "RAZORPAY_KEY_ID not configured"));
    }
    return ResponseEntity.ok(Map.of("key", key));
  }
}

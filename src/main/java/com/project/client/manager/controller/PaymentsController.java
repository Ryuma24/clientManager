package com.project.client.manager.controller;

import com.nimbusds.oauth2.sdk.Response;
import com.project.client.manager.model.PaypalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payments")
public class PaymentsController {

    @PostMapping("/captured")
    public ResponseEntity<String> payPaylPaymentDone(@RequestBody PaypalResponse paypalResponse){
            return ResponseEntity.ok("Payment for webhook " + paypalResponse.getWebhook_id() + " is completed");
    }



}

package org.ai.server.controller;

import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import org.ai.server.configuration.JwtTokenProvider;
import org.ai.server.dto.Response;
import org.ai.server.model.UserEntity;
import org.ai.server.repository.UserRepository;
import org.ai.server.service.PurchaseService;
import org.ai.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
@AllArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
private final UserService userService;
private final UserRepository userRepository;


    @PostMapping
    public ResponseEntity<Response> purchaseCourse(@RequestParam Long courseId,
                                                   @RequestHeader("Authorization") String authHeader) {
        try {


            String email= userService.getUserDataWithToken(authHeader);
            UserEntity user=userRepository.findByEmail(email);
            Response response = purchaseService.initiatePurchase(
                courseId,
                    user.getId()
            );
          return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (StripeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new Response());

        }
    }

    // Callback endpoint for Stripe (would be called by Stripe after payment)
    @PostMapping("/complete")
    public ResponseEntity<Response> completePurchase(@RequestParam Long purchaseId) {
        Response response=purchaseService.completePurchase(purchaseId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/fail")
    public ResponseEntity<Response> failPurchase(@RequestParam Long purchaseId) {
        Response response=purchaseService.failPurchase(purchaseId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }




}
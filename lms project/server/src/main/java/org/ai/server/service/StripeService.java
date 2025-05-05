package org.ai.server.service;

import com.stripe.exception.StripeException;
import org.ai.server.dto.Response;

public interface StripeService {
     Response createCheckoutSession(Long purchaseId, String courseTitle, Double amount, String successUrl, String cancelUrl) throws StripeException;
}

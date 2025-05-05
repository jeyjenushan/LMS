package org.ai.server.service;

import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import org.ai.server.dto.Response;
import org.ai.server.repository.PurchaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface PurchaseService {

    Response initiatePurchase(
            Long courseId,
            Long userId
    ) throws StripeException;

    Response completePurchase(Long purchaseId);
    Response failPurchase(Long purchaseId);

}

package org.ai.server.serviceImplementation;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.ai.server.dto.Response;
import org.ai.server.service.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class StripeServiceImplementation implements StripeService {

    @Value("${STRIPE_API_KEY}")
    private String stripeSecretKey;

    @Value("${app.currency}")
    private String currency;

    public Response createCheckoutSession(Long purchaseId, String courseTitle, Double amount, String successUrl, String cancelUrl) throws StripeException {

        Response response=new Response();

        try{
            Stripe.apiKey = stripeSecretKey;

            List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
            lineItems.add(
                    SessionCreateParams.LineItem.builder()
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency(currency)
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(courseTitle)
                                                            .build())
                                            .setUnitAmount((long)(amount * 100))
                                            .build())
                            .setQuantity(1L)
                            .build()
            );

            SessionCreateParams params = SessionCreateParams.builder()
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .addAllLineItem(lineItems)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .putMetadata("purchaseId", purchaseId.toString())
                    .build();

            Session session = Session.create(params);

            // Setting response with relevant data
            response.setStatusCode(200);
            response.setMessage("Stripe session created successfully");
            response.setStripeUrl( session.getUrl());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());


        }
        return response;



    }
}

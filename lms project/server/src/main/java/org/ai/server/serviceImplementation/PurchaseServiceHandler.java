package org.ai.server.serviceImplementation;
import com.stripe.exception.StripeException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.ai.server.dto.PurchaseDto;
import org.ai.server.dto.Response;
import org.ai.server.enumpackage.Status;
import org.ai.server.mapper.DtoConverter;
import org.ai.server.model.CourseEntity;
import org.ai.server.model.PurchaseEntity;
import org.ai.server.model.UserEntity;
import org.ai.server.repository.CourseRepository;
import org.ai.server.repository.PurchaseRepository;
import org.ai.server.repository.UserRepository;
import org.ai.server.service.CourseControllerService;
import org.ai.server.service.PurchaseService;
import org.ai.server.service.StripeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class PurchaseServiceHandler implements PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final StripeService stripeService;
    private final CourseControllerService courseControllerService;




    @Override
    public Response initiatePurchase(Long courseId, Long userId) throws StripeException {

        Response response = new Response();

        try{
            CourseEntity course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found"));

            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));


            // Check if already enrolled
            if (user.getEnrolledCourses().contains(course)) {
                response.setStatusCode(400);
                response.setSuccess(false);
                response.setMessage("User already enrolled in this course");
                return response;
            }
            // Calculate amount with discount
            double amount = course.getPrice() - (course.getDiscount() * course.getPrice() / 100);
            amount = Math.round(amount * 100.0) / 100.0;

            // Create purchase record
            PurchaseEntity purchase = new PurchaseEntity();
            purchase.setUser(user);
            purchase.setCourse(course);
            purchase.setAmount(amount);
            purchase.setStatus(Status.PENDING);
            purchase = purchaseRepository.save(purchase);

            // Prepare URLs
            String successUrl = "http://localhost:5173" + "/verify?success=true&purchaseId=" + purchase.getId();
            String cancelUrl = "http://localhost:5173" + "/verify?success=false&purchaseId=" + purchase.getId();

            // Create Stripe session
            Response stripeResponse = stripeService.createCheckoutSession(
                    purchase.getId(),
                    course.getTitle(),
                    amount,
                    successUrl,
                    cancelUrl
            );


            Map<String, Object> data = new HashMap<>();
            data.put("sessionUrl", stripeResponse.getStripeUrl());
            data.put("purchaseId", purchase.getId());



            response.setStatusCode(200);

            response.setSuccess(true);
            response.setMessage("Checkout session created successfully");
            response.setData(data);

        } catch (EntityNotFoundException e) {
            response.setStatusCode(404);
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        } catch (StripeException e) {
            response.setStatusCode(500);
            response.setSuccess(false);
            response.setMessage("Error creating Stripe checkout session: " + e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setSuccess(false);
            response.setMessage("Error processing purchase: " + e.getMessage());
        }

        return response;
    }

    @Override
    public Response completePurchase(Long purchaseId) {

        Response response = new Response();
        try{
            // Find the purchase record
            PurchaseEntity purchase = purchaseRepository.findById(purchaseId)
                    .orElseThrow(() -> new EntityNotFoundException("Purchase not found with id: " + purchaseId));

            // Update purchase status to COMPLETED
            purchase.setStatus(Status.COMPLETED);
            purchaseRepository.save(purchase);

            // Get user and course from purchase
            UserEntity user = purchase.getUser();
            CourseEntity course = purchase.getCourse();

            // Add course to user's enrolled courses if not already present
            if (!user.getEnrolledCourses().contains(course)) {
                user.getEnrolledCourses().add(course);
                userRepository.save(user);
            }

            // Add user to course's students if not already present
            if (!course.getStudents().contains(user)) {
                course.getStudents().add(user);
                courseRepository.save(course);
            }
           PurchaseDto purchaseDto=DtoConverter.convertThePurchaseToPurchaseDto(purchase);
            response.setStatusCode(200);
            response.setPurchaseDto(purchaseDto);
            response.setSuccess(true);
            response.setMessage("Purchase completed successfully");


        }catch (EntityNotFoundException e) {

            response.setStatusCode(404);
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        catch (Exception e) {
            response.setStatusCode(500);
            response.setSuccess(false);
            response.setMessage("Error processing purchase: " + e.getMessage());
        }


 return  response;


    }

    @Override
    public Response failPurchase(Long purchaseId) {

        Response response=new Response();

        try{
            PurchaseEntity purchase = purchaseRepository.findById(purchaseId)
                    .orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

            purchase.setStatus(Status.FAILED);
            purchaseRepository.save(purchase);
            PurchaseDto purchaseDto=DtoConverter.convertThePurchaseToPurchaseDto(purchase);
            response.setStatusCode(200);
            response.setPurchaseDto(purchaseDto);
            response.setSuccess(true);
            response.setMessage("Purchase not completed ");


        }catch (EntityNotFoundException e) {

            response.setStatusCode(404);
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        catch (Exception e) {
            response.setStatusCode(500);
            response.setSuccess(false);
            response.setMessage("Error processing purchase: " + e.getMessage());
        }
        return response;


    }
}

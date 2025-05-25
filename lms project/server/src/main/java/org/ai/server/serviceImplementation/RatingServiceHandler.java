package org.ai.server.serviceImplementation;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.ai.server.configuration.JwtTokenProvider;
import org.ai.server.dto.CourseRatingDto;
import org.ai.server.dto.Response;
import org.ai.server.mapper.DtoConverter;
import org.ai.server.model.CourseEntity;
import org.ai.server.model.CourseRatingEntity;
import org.ai.server.model.UserEntity;
import org.ai.server.repository.CourseRatingRepository;
import org.ai.server.repository.CourseRepository;
import org.ai.server.repository.UserRepository;
import org.ai.server.service.RatingService;
import org.ai.server.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class RatingServiceHandler implements RatingService {

    private final CourseRatingRepository courseRatingRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;


    @Override
    public Response addRating(String email, Long courseId, Integer rating){
        Response response = new Response();
        try {

            if (rating < 1 || rating > 5) {
                 response.setStatusCode(400);
                       response .setSuccess(false);
                        response.setMessage("Rating must be between 1 and 5");
                        return response;
            }



            UserEntity user = userRepository.findByEmail(email);

            if (user == null) {
                 response.setStatusCode(404);
                        response.setSuccess(false);
                       response .setMessage("User not found");
                       return response;
            }


            CourseEntity course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found"));


            if (!user.getEnrolledCourses().contains(course)) {
                 response.setStatusCode(403);
                        response.setSuccess(false);
                        response.setMessage("User has not purchased this course");
                        return response;
            }


            CourseRatingEntity rating1 = courseRatingRepository
                    .findByUserAndCourse(user, course)
                    .orElse(new CourseRatingEntity(user, course));

            course.getRatings().add(rating1);

            rating1.setRating(rating);

           CourseRatingEntity courseRatingEntity= courseRatingRepository.save(rating1);
            CourseRatingDto courseRatingDto= DtoConverter.convertTheCourseRatingToCourseRatingDto(courseRatingEntity);
            response.setCourseRatingDto(courseRatingDto);



             response.setStatusCode(200);
                  response.setSuccess(true);
                    response.setMessage("Rating added successfully");
                    return response;

        } catch (EntityNotFoundException e) {
             response.setStatusCode(404);
                    response.setSuccess(false);
                    response.setMessage(e.getMessage());
                    return response;
        } catch (Exception e) {
             response.setStatusCode(500);
                   response .setSuccess(false);
                    response.setMessage("Error adding rating: " + e.getMessage());
                    return response;
        }
    }

}

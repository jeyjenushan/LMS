package org.ai.server.serviceImplementation;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.ai.server.dto.CourseProgressDto;
import org.ai.server.dto.Response;
import org.ai.server.mapper.DtoConverter;
import org.ai.server.model.*;
import org.ai.server.repository.CourseProgressRepository;
import org.ai.server.repository.CourseRepository;
import org.ai.server.repository.LectureRepository;
import org.ai.server.repository.UserRepository;
import org.ai.server.service.CourseProgressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
@AllArgsConstructor
public class CourseProgressServiceHandler implements CourseProgressService {

    private final CourseProgressRepository courseProgressRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;



    @Override
    public Response updateCourseProgress(String email, Long lectureId) {
        Response response = new Response();
        try {
            // 1. Validate user exists
            UserEntity user = userRepository.findByEmail(email);
            if (user == null) {
               response.setMessage("User not found");
               response.setSuccess(false);
               response.setStatusCode(400);
               return response;
            }


            // 2. Validate lecture exists and get its course
            LectureEntity lecture = lectureRepository.findById(lectureId)
                    .orElseThrow(() -> new EntityNotFoundException("Lecture not found"));
            CourseEntity course = lecture.getChapter().getCourse();

            // 3. Find or create progress record
            CourseProgressEntity progress = courseProgressRepository
                    .findByUserAndCourse(user, course)
                    .orElseGet(() -> {
                        CourseProgressEntity newProgress = new CourseProgressEntity();
                        newProgress.setUser(user);
                        newProgress.setCourse(course);
                        return courseProgressRepository.save(newProgress);
                    });

            // 4. Check if lecture already completed
            if (progress.getCompletedLectures().contains(lecture)) {
                response.setStatusCode(200);
                response.setSuccess(false);
                response.setMessage("Lecture already completed");

                // Convert to DTO and return immediately
                CourseProgressDto courseProgressDto = DtoConverter.convertTheCourseProgressToCourseProgressDto(progress);
                courseProgressDto.getCompletedLectures().add(DtoConverter.convertTheLectureToLectureDto(lecture));
                response.setCourseProgressDto(courseProgressDto);
                return response;  // Add this return statement to exit early
            }

            // 5. Update progress
            progress.getCompletedLectures().add(lecture);

            // 6. Check if course is completed (all lectures finished)
            long totalLecturesInCourse = course.getChapters().stream()
                    .flatMap(chapter -> chapter.getLectures().stream())
                    .count();

            progress.setCompleted(progress.getCompletedLectures().size() == totalLecturesInCourse);
            courseProgressRepository.save(progress);

             response.setStatusCode(200);
                    response.setSuccess(true);
                    response.setMessage("Progress updated successfully");
            CourseProgressDto courseProgressDto= DtoConverter.convertTheCourseProgressToCourseProgressDto(progress);
            courseProgressDto.getCompletedLectures().add(DtoConverter.convertTheLectureToLectureDto(lecture));
            response.setCourseProgressDto(courseProgressDto);
                    return response;

        } catch (EntityNotFoundException e) {
             response.setStatusCode(404);
       response.setSuccess(false);
               response.setMessage(e.getMessage());
        } catch (Exception e) {
             response.setStatusCode(500);
                    response.setSuccess(false);
                    response.setMessage("Error updating progress: " + e.getMessage());
        }
        return response;
    }





    @Override
    public Response getCourseProgress(String email, Long courseId) {
        Response response = new Response();
        try {
            // 1. Validate user exists
            UserEntity user = userRepository.findByEmail(email);
            if (user == null) {
                 response.setStatusCode(404);
                        response.setSuccess(false);
                        response.setMessage("User not found");
                 return response;
            }

            // 2. Validate course exists
            CourseEntity course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found"));

            // 3. Check if user is enrolled
            if (!user.getEnrolledCourses().contains(course)) {
                 response.setStatusCode(403);
                response    .setSuccess(false);
                response   .setMessage("User is not enrolled in this course");
                 return response;
            }

            // 4. Get  progress record
            // 4. Get progress record or return empty progress if none exists
            Optional<CourseProgressEntity> progressOpt = courseProgressRepository.findByUserAndCourse(user, course);






            // 5. Prepare response
            CourseProgressDto dto = new CourseProgressDto();

            dto.setCourse(DtoConverter.convertTheCourseToCourseDto(course));
            dto.setUser(DtoConverter.convertTheUserToUserDto(user));

            if (progressOpt.isPresent()) {
                CourseProgressEntity progress = progressOpt.get();
                dto.setId(progress.getId());
                dto.setCompletedLectures(DtoConverter.convertTheLectureListToLectureDtoList(progress.getCompletedLectures()));
                dto.setCompletedCount(progress.getCompletedLectures().size());
                dto.setId(progress.getId());
            } else {
                // Return empty progress
                dto.setCompletedLectures(new ArrayList<>());
                dto.setCompletedCount(0);
            }

            response.setCourseProgressDto(dto);
            response.setStatusCode(200);
            response.setSuccess(true);
            response.setMessage("Progress retrieved successfully");
            return response;


        } catch (EntityNotFoundException e) {
             response.setStatusCode(404);
            response.setSuccess(false);
            response .setMessage(e.getMessage());
        } catch (Exception e) {
             response.setStatusCode(500);
            response  .setSuccess(false);
            response .setMessage("Error retrieving progress: " + e.getMessage());
        }
        return response;
    }

}

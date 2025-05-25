package org.ai.server.serviceImplementation;

import lombok.AllArgsConstructor;
import org.ai.server.dto.*;
import org.ai.server.enumpackage.Status;
import org.ai.server.mapper.DtoConverter;
import org.ai.server.model.CourseEntity;
import org.ai.server.model.PurchaseEntity;
import org.ai.server.model.UserEntity;
import org.ai.server.repository.CourseRepository;
import org.ai.server.repository.PurchaseRepository;
import org.ai.server.repository.UserRepository;
import org.ai.server.service.EducatorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EducatorServiceHandler implements EducatorService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PurchaseRepository purchaseRepository;
    @Override
    public Response getEducatorCourse( String email) {
       Response response = new Response();
       try{
           UserEntity user=userRepository.findByEmail(email);
           List<CourseEntity>courseEntities=courseRepository.findByEducatorId(user.getId());
           if(courseEntities.isEmpty()){
               response.setMessage("No courses found with specific educator");
               response.setStatusCode(200);
               response.setSuccess(true);
               response.setCourseDtoList(Collections.EMPTY_LIST);
         return response;
           }
       List<CourseDto>courseDtoList= DtoConverter.convertTheCourseListToCourseDtoList(courseEntities);
           response.setStatusCode(200);
           response.setSuccess(true);
           response.setCourseDtoList(courseDtoList);
           response.setMessage("Successfully retrieved specific educator course");
           return response;


       }catch(Exception e){
           response.setStatusCode(500);
           response.setSuccess(false);
           response.setMessage("Cannot retrieving specific educator course please try again");

       }
       return response;
    }


    @Override
    public Response getDashboardData(String email) {
        Response response = new Response();
        try{
            UserEntity educator=userRepository.findByEmail(email);
            if (educator == null) {
                response.setStatusCode(404);
                response.setSuccess(false);
                response.setMessage("Educator not found");
                return response;
            }
            Long educatorId=educator.getId();
            List<CourseEntity>courseEntities=courseRepository.findByEducatorId(educatorId);
            int totalCourses=courseEntities.size();

            //CalculateTotalEarningsAndFromCompletedPurchases
            List<Long> courseIds=courseEntities.stream().map(CourseEntity::getId).collect(Collectors.toList());
            List<PurchaseEntity> purchases = purchaseRepository.findByCourseIdInAndStatus(courseIds, Status.COMPLETED);
            double totalEarnings = purchases.stream()
                    .mapToDouble(PurchaseEntity::getAmount)
                    .sum();
            // Collect enrolled students with course titles
            List<EnrolledStudentsDto>enrolledStudentsDtoList=new ArrayList<>();
            for(CourseEntity courseEntity:courseEntities){
                List<UserEntity>students=userRepository.findAllById(courseEntity.getStudents().stream().map(UserEntity::getId).collect(Collectors.toList()));
               for(UserEntity userEntity:students){
                   EnrolledStudentsDto enrolledStudentsDto=new EnrolledStudentsDto();
                   enrolledStudentsDto.setCourseTitle(courseEntity.getTitle());
                   enrolledStudentsDto.setStudent(userEntity);
                   enrolledStudentsDtoList.add(enrolledStudentsDto);
               }
            }
            // Create dashboard DTO
           EducatorDashboardDataDto dashboardData = new EducatorDashboardDataDto();
            dashboardData.setTotalEarnings(totalEarnings);
            dashboardData.setEnrolledStudentsDtoList(enrolledStudentsDtoList);
            dashboardData.setTotalCourses(totalCourses);

            response.setStatusCode(200);
            response.setSuccess(true);
            response.setMessage("Educator dashboard data retrieved successfully");
            response.setEducatorDashboardDataDto(dashboardData);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setSuccess(false);
            response.setMessage("Error retrieving educator dashboard data: " + e.getMessage());
        }
        return response;
    }


    @Override
     public  Response getEnrolledStudentsWithPurchaseData(String email) {
       Response response=new Response();
       try{
           // Get educator user
           UserEntity educator = userRepository.findByEmail(email);
           if (educator == null) {
               response.setStatusCode(404);
               response.setSuccess(false);
               response.setMessage("Educator not found");
               return response;
           }
           // Get educator's courses
           List<CourseEntity> courses = courseRepository.findByEducatorId(educator.getId());
           // Fetch completed purchases with user and course data
           List<Long> courseIds = courses.stream()
                   .map(CourseEntity::getId)
                   .collect(Collectors.toList());
           // Fetch completed purchases with user and course data
           List<PurchaseEntity> purchases = purchaseRepository.findByCourseIdInAndStatus(courseIds, Status.COMPLETED);

           List<EnrolledStudentsDto> enrolledStudentsDtoList=purchases.stream().map(
                   purchase->{
                       EnrolledStudentsDto dto=new EnrolledStudentsDto();
                       dto.setStudent(purchase.getUser());
                       dto.setCourseTitle(purchase.getCourse().getTitle());
                       dto.setPurchaseDate(purchase.getCreatedAt());
                       return dto;
                   }
           ).collect(Collectors.toList());

           response.setStatusCode(200);
           response.setSuccess(true);
           response.setMessage("Enrolled students data retrieved successfully");
           response.setEnrolledStudentsDtoList(enrolledStudentsDtoList);




       }catch (Exception e){
           response.setStatusCode(500);
           response.setSuccess(false);
           response.setMessage("Error retrieving enrolled students data: " + e.getMessage());
       }

 return response;

    }


}

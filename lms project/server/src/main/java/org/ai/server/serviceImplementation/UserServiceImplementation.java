package org.ai.server.serviceImplementation;

import lombok.AllArgsConstructor;
import org.ai.server.configuration.JwtTokenProvider;
import org.ai.server.dto.CourseDto;
import org.ai.server.dto.Response;
import org.ai.server.dto.UserDto;
import org.ai.server.mapper.DtoConverter;
import org.ai.server.model.CourseEntity;
import org.ai.server.model.UserEntity;
import org.ai.server.repository.UserRepository;
import org.ai.server.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    public Response getUserData(String email) {
        Response response = new Response();
        try {
            UserEntity user = userRepository.findByEmail(email);
            if (user == null) {
                response.setStatusCode(404);
                response.setSuccess(false);
                response.setMessage("User not found");
                return response;
            }

            List<CourseEntity> courseEntityList = user.getEnrolledCourses();
            UserDto userDto = DtoConverter.convertTheUserToUserDto(user);

            // Convert courses - handles both null and empty list cases
            List<CourseDto> courseDtos = courseEntityList != null
                    ? DtoConverter.convertTheCourseListToCourseDtoList(courseEntityList)
                    : Collections.emptyList();

            userDto.setEnrolledCourses(courseDtos);

            response.setStatusCode(200);
            response.setSuccess(true);
            response.setMessage("User Details successfully fetched");
            response.setUserDto(userDto);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setSuccess(false);
            response.setMessage("User Details cannot be fetched. Please try again");

        }

        return response;
    }

    @Override
    public String  getUserDataWithToken(String token) {
        token=token.substring(7);
        return JwtTokenProvider.extractUsername(token);
    }

    @Override
    public Response getEnrolledCourses(String email) {
        Response response=new Response();
        try{
            UserEntity user=userRepository.findByEmail(email);
            if(user==null){
                response.setStatusCode(404);
                response.setMessage("User not found");
                response.setSuccess(false);
                return response;
            }
            List<CourseEntity>courseEntities=user.getEnrolledCourses();

            if(courseEntities.isEmpty()){
                response.setStatusCode(404);
                response.setMessage("Courses not enrolled");
                response.setSuccess(false);
                return response;
            }
            response.setStatusCode(200);
            response.setSuccess(true);
            response.setMessage("Courses successfully fetched");
            response.setCourseDtoList(DtoConverter.convertTheCourseListToCourseDtoList(courseEntities));
            return response;

        } catch (Exception e) {
            response.setStatusCode(200);
            response.setSuccess(false);
            response.setMessage("User could not be enrolled");
            response.setSuccess(false);
            return response;


        }
    }
}

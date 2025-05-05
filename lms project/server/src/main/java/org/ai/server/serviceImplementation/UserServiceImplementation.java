package org.ai.server.serviceImplementation;

import lombok.AllArgsConstructor;
import org.ai.server.configuration.JwtTokenProvider;
import org.ai.server.dto.Response;
import org.ai.server.dto.UserDto;
import org.ai.server.mapper.DtoConverter;
import org.ai.server.model.CourseEntity;
import org.ai.server.model.UserEntity;
import org.ai.server.repository.UserRepository;
import org.ai.server.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    @Override
    public Response getUserData(String email) {
        Response response=new Response();
        try{
            UserEntity user=userRepository.findByEmail(email);
            List<CourseEntity>courseEntityList=user.getEnrolledCourses();
            if(user==null){
                response.setStatusCode(404);
                response.setMessage("User not found");
                response.setSuccess(false);
                return response;
            }
            UserDto userDto= DtoConverter.convertTheUserToUserDto(user);
            userDto.setEnrolledCourses(DtoConverter.convertTheCourseListToCourseDtoList(courseEntityList));
            response.setStatusCode(200);
            response.setSuccess(true);
            response.setMessage("User Details successfully fetched");
            response.setUserDto(userDto);
            return response;

        }catch (Exception e){
            response.setStatusCode(200);
            response.setSuccess(false);
            response.setMessage("User Details can not be fetched please try again");

            return response;
        }

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

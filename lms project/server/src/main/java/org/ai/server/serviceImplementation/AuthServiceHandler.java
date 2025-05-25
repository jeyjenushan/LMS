package org.ai.server.serviceImplementation;

import lombok.AllArgsConstructor;
import org.ai.server.configuration.JwtTokenProvider;
import org.ai.server.dto.Response;
import org.ai.server.dto.UserDto;
import org.ai.server.enumpackage.ROLE;
import org.ai.server.mapper.DtoConverter;
import org.ai.server.model.UserEntity;
import org.ai.server.repository.UserRepository;
import org.ai.server.request.LoginRequest;
import org.ai.server.service.AuthService;
import org.ai.server.service.CloudinaryService;
import org.ai.server.service.EmailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
@AllArgsConstructor
public class AuthServiceHandler implements AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final CloudinaryService cloudinaryService;
    @Override
    public Response LoginUser(LoginRequest loginRequest) {
        Response response = new Response();
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()
                    )
            );


            UserEntity userEntity = userRepository.findByEmail(loginRequest.getEmail());


            String token = jwtTokenProvider.generateToken(userEntity);
            Date expirationDate = JwtTokenProvider.extractExpiration(token);


            UserDto userDto = DtoConverter.convertTheUserToUserDto(userEntity);



            response.setStatusCode(200);
            response.setToken(token);
            response.setExpirationTime(String.valueOf(expirationDate));
            response.setUserDto(userDto);
            response.setMessage("The account has been logged in successfully.");

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Login failed: " + e.getMessage());
        }

        return response;

    }

    @Override
    public Response RegisterUser(UserEntity user, MultipartFile imageFile) {
        Response response=new Response();


        try{
            if (userRepository.existsByEmail(user.getEmail())) {
                 response.setStatusCode(400);
                 response.setMessage("Email already in use.");
                 return response;
            }

            if (imageFile == null || imageFile.isEmpty()) {
                response.setMessage("Please provide an image file");
                return response;
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if(user.getRole()== ROLE.EDUCATOR){
                user.setRole(ROLE.EDUCATOR);
            }
            else{
                user.setRole(ROLE.STUDENT);
            }

            String thumbnailUrl = cloudinaryService.uploadFile(imageFile);

            user.setImage(thumbnailUrl);

            UserEntity savedUser=userRepository.save(user);
            UserDto userDto = DtoConverter.convertTheUserToUserDto(savedUser);
            response.setStatusCode(200);
            response.setMessage("User has been registered successfully.");
            response.setUserDto(userDto);
            return response;


        }catch(Exception e){
            response.setStatusCode(400);
            response.setMessage("User has not been registered successfully.");

        }
        return response;
    }


}

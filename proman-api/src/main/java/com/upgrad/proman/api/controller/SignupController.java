package com.upgrad.proman.api.controller;

import com.upgrad.proman.api.model.SignupUserRequest;
import com.upgrad.proman.api.model.SignupUserResponse;
import com.upgrad.proman.service.business.SignUpBusinessService;
import com.upgrad.proman.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.time.ZonedDateTime;
import java.util.UUID;

@RestController// Annotation tells this class is a controller,
// and all methods inside this class return model objects
// An alternative way would be
//@Controller
// and annotate each method with @ResponseBody
@RequestMapping("/")
public class SignupController {

    @Autowired
    private SignUpBusinessService signUpBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/signup",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(SignupUserRequest signupUserRequest){
        UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());

        userEntity.setSalt("1234abc");
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setStatus(4);
        userEntity.setCreatedAt(ZonedDateTime.now());
        userEntity.setCreatedBy("api-backend");
        userEntity.setMobilePhone(signupUserRequest.getMobileNumber());

        //userEntity1 is persisted
        final UserEntity userEntity1 = signUpBusinessService.signup(userEntity);
        SignupUserResponse signupUserResponse = new SignupUserResponse();
        signupUserResponse.setId(userEntity1.getUuid());
        signupUserResponse.setStatus("Registered");


        return new ResponseEntity(signupUserResponse,HttpStatus.CREATED);
    }
}

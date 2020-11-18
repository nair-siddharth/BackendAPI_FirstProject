package com.upgrad.proman.api.controller;

import com.upgrad.proman.api.model.CreateUserRequest;
import com.upgrad.proman.api.model.CreateUserResponse;
import com.upgrad.proman.api.model.UserDetailsResponse;
import com.upgrad.proman.api.model.UserStatusType;
import com.upgrad.proman.service.business.UserAdminBusinessService;
import com.upgrad.proman.service.entity.RoleEntity;
import com.upgrad.proman.service.entity.UserEntity;
import com.upgrad.proman.service.exception.ResourceNotFoundException;
import com.upgrad.proman.service.exception.UnauthorizedException;
import com.upgrad.proman.service.type.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserAdminController {

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

    @RequestMapping(method = RequestMethod.GET,  path = "/users/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(
            @PathVariable("id") final String UUID,
            @RequestHeader("authorization") final String authorization)
            throws ResourceNotFoundException, UnauthorizedException {
        // This method returns user details IFF the current user is an admin.
        // This is checked by checking the JWT sent. The received format is as follows
        // Bearer <JWT String>
        String jwToken = authorization.split("Bearer ")[1];

        final UserEntity userEntity = userAdminBusinessService.getUser(UUID, jwToken);
        UserDetailsResponse userDetailsResponse =  new UserDetailsResponse().id(userEntity.getUuid()).
                firstName(userEntity.getFirstName()).lastName(userEntity.getLastName()).
                emailAddress(userEntity.getEmail()).mobileNumber(userEntity.getMobilePhone()).
                status(UserStatusType.valueOf(UserStatus.getEnum(userEntity.getStatus()).name()));
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/users", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody final CreateUserRequest userRequest){
        UserEntity newUser = new UserEntity();

        newUser.setEmail(userRequest.getEmailAddress());
        newUser.setMobilePhone(userRequest.getMobileNumber());
        newUser.setFirstName(userRequest.getFirstName());
        newUser.setLastName(userRequest.getLastName());
        newUser.setStatus(UserStatus.ACTIVE.getCode());
        newUser.setCreatedAt(ZonedDateTime.now());
        newUser.setCreatedBy("api-backend");
        newUser.setUuid(UUID.randomUUID().toString());


        UserEntity createdUser = userAdminBusinessService.createUser(newUser);

        CreateUserResponse userResponse = new CreateUserResponse();
        userResponse.setId(createdUser.getUuid());
        userResponse.setStatus(UserStatusType.ACTIVE);
        return new ResponseEntity(userResponse,HttpStatus.CREATED);
    }
}

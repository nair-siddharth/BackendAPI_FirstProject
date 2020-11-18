package com.upgrad.proman.api.controller;

import com.upgrad.proman.api.model.AuthorizedUserResponse;
import com.upgrad.proman.service.business.AuthenticationService;
import com.upgrad.proman.service.entity.UserAuthTokenEntity;
import com.upgrad.proman.service.entity.UserEntity;
import com.upgrad.proman.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(method = RequestMethod.POST, path = "/auth/login",
    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AuthorizedUserResponse> login(@RequestHeader("authorization") final String authorizationHeader)
            throws AuthenticationFailedException {
        //Basic dXNlcm5hbWU6cGFzc3dvcmQ=
        //above is a sample encoded text where the username is "username" and password is "password" separated by a ":"
        byte[] decode = Base64.getDecoder().decode(authorizationHeader.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        UserAuthTokenEntity userAuthTokenEntity= authenticationService.authenticate(decodedArray[0],decodedArray[1]);
        UserEntity user = userAuthTokenEntity.getUser();

        AuthorizedUserResponse authorizedUserResponse = new AuthorizedUserResponse();
        authorizedUserResponse.setId(UUID.fromString(user.getUuid()));
        authorizedUserResponse.setFirstName(user.getFirstName());
        authorizedUserResponse.setLastName(user.getLastName());
        authorizedUserResponse.setEmailAddress(user.getEmail());
        authorizedUserResponse.setMobilePhone(user.getMobilePhone());
        authorizedUserResponse.setLastLoginTime(user.getLastLoginAt());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("access-token",userAuthTokenEntity.getAccessToken());

        return new ResponseEntity<AuthorizedUserResponse>(authorizedUserResponse,httpHeaders, HttpStatus.OK);
    }


}

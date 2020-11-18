package com.upgrad.proman.service.business;

import com.upgrad.proman.service.common.JwtTokenProvider;
import com.upgrad.proman.service.dao.UserDAO;
import com.upgrad.proman.service.entity.UserAuthTokenEntity;
import com.upgrad.proman.service.entity.UserEntity;
import com.upgrad.proman.service.exception.AuthenticationFailedException;
import com.upgrad.proman.service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class AuthenticationService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticate(final String username, final String password)
            throws AuthenticationFailedException {
        UserEntity userEntity = userDAO.getUserByEmail(username);
        if(userEntity==null){
            throw new AuthenticationFailedException("AUTH-001","Incorrect username/email id");
        }
        final   String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
        //
        if(encryptedPassword.equals(userEntity.getPassword())){
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
            userAuthToken.setUser(userEntity);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthToken.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));

            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);
            userAuthToken.setCreatedBy("api-backend");
            userAuthToken.setCreatedAt(now);

            userDAO.createAuthToken(userAuthToken);

            userEntity.setLastLoginAt(now);
            userDAO.updateUser(userEntity);

            return userAuthToken;
        }else{
            throw new AuthenticationFailedException("AUTH-002","Incorrect Password");
        }
    }

}

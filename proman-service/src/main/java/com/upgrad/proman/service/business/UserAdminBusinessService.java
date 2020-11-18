package com.upgrad.proman.service.business;

import com.upgrad.proman.service.dao.UserDAO;
import com.upgrad.proman.service.entity.RoleEntity;
import com.upgrad.proman.service.entity.UserAuthTokenEntity;
import com.upgrad.proman.service.entity.UserEntity;
import com.upgrad.proman.service.exception.ResourceNotFoundException;
import com.upgrad.proman.service.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    public UserEntity getUser(final String UUID, final String jwToken)
            throws ResourceNotFoundException, UnauthorizedException {

        // getUser allows anyone with UUID to get the user info
        // UserEntity userEntity = userDAO.getUser(UUID);


        UserAuthTokenEntity userAuthTokenEntity = userDAO.getUserByAuthToken(UUID,jwToken);
        if(userAuthTokenEntity==null){
            throw new UnauthorizedException("USR-002","Invalid Session Token");
        }
        RoleEntity role = userAuthTokenEntity.getUser().getRole();
        if(role!=null && role.getUuid() == 101){
            UserEntity userEntity =  userDAO.getUser(UUID);
            if(userEntity == null){
                throw new ResourceNotFoundException("USR-001", "User not found");
            }
            return userEntity;
        }
        throw new UnauthorizedException("ATH-002", "you are not authorized to fetch user details");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(final UserEntity user){

        // If user is created using signup endpoint, password will not be null
        // If user created without signup (created by admin)
        // In this case, the password will be null, as admin cannot select a password for the user
        // API endpoint will assign a default password, which the user should change
        final String password = user.getPassword();
        if(password == null){
            user.setPassword("proman@123");
        }
        String[] encryptedText = cryptographyProvider.encrypt(user.getPassword());
        user.setSalt(encryptedText[0]);
        user.setPassword(encryptedText[1]);
        return userDAO.createUser(user);
    }
}

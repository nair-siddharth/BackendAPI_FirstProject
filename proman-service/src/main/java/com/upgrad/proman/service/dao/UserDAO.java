package com.upgrad.proman.service.dao;

import com.upgrad.proman.service.entity.UserAuthTokenEntity;
import com.upgrad.proman.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity){
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserEntity getUser(final String UUID){
        try{
            return entityManager.createNamedQuery("userByUuid",UserEntity.class)
                    .setParameter("uuid",UUID).getSingleResult();

//            return entityManager.createNamedQuery("userAuthTokenByAccessToken",UserAuthTokenEntity.class).setParameter()

        }catch (NoResultException e){
            return null;
        }

    }

    public UserAuthTokenEntity getUserByAuthToken(final String UUID, final String jwToken){
        try{
            return entityManager.createNamedQuery("userAuthTokenByAccessToken",UserAuthTokenEntity.class).
                    setParameter("accessToken",jwToken).getSingleResult();
        }catch (NoResultException e){
            return null;
        }

    }

    public UserEntity getUserByEmail(final String email) {// email is the username
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).
                    setParameter("email", email).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity){
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    public void updateUser(final UserEntity updatedUserEntity){
        entityManager.merge(updatedUserEntity);
    }

}

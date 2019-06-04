package br.com.oauth2.mongo.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoryBase {

    boolean changePassword(String oldPassword, String newPassword, String username);

}

package br.com.oauth2.mongo.repository;

import org.springframework.stereotype.Repository;

import br.com.oauth2.mongo.entity.mongo.MongoOAuth2ClientToken;
@Repository
public interface MongoOAuth2ClientTokenRepositoryBase {
    boolean deleteByAuthenticationId(String authenticationId);

    MongoOAuth2ClientToken findByAuthenticationId(String authenticationId);
}

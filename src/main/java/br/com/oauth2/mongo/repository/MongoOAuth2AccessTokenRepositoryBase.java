package br.com.oauth2.mongo.repository;


import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.oauth2.mongo.entity.mongo.MongoOAuth2AccessToken;
@Repository
public interface MongoOAuth2AccessTokenRepositoryBase {
    MongoOAuth2AccessToken findByTokenId(String tokenId);

    boolean deleteByTokenId(String tokenId);

    boolean deleteByRefreshTokenId(String refreshTokenId);

    MongoOAuth2AccessToken findByAuthenticationId(String key);

    List<MongoOAuth2AccessToken> findByUsernameAndClientId(String username, String clientId);

    List<MongoOAuth2AccessToken> findByClientId(String clientId);
}

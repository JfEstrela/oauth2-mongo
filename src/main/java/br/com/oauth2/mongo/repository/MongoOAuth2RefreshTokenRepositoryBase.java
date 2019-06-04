package br.com.oauth2.mongo.repository;

import org.springframework.stereotype.Repository;

import br.com.oauth2.mongo.entity.mongo.MongoOAuth2RefreshToken;
@Repository
public interface MongoOAuth2RefreshTokenRepositoryBase {
	
    MongoOAuth2RefreshToken findByTokenId(String tokenId);

    boolean deleteByTokenId(String tokenId);
}

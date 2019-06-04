package br.com.oauth2.mongo.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.oauth2.mongo.entity.mongo.MongoOAuth2RefreshToken;

@Repository
public interface MongoOAuth2RefreshTokenRepository extends MongoRepository<MongoOAuth2RefreshToken, String>, MongoOAuth2RefreshTokenRepositoryBase {
}

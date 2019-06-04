package br.com.oauth2.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.oauth2.mongo.entity.mongo.MongoOAuth2ClientToken;
@Repository
public interface MongoOAuth2ClientTokenRepository extends MongoRepository<MongoOAuth2ClientToken, String>, MongoOAuth2ClientTokenRepositoryBase {
}

package br.com.oauth2.mongo.repository;

import com.mongodb.client.result.DeleteResult;

import br.com.oauth2.mongo.entity.mongo.MongoOAuth2ClientToken;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class MongoOAuth2ClientTokenRepositoryImpl implements MongoOAuth2ClientTokenRepositoryBase {

    private final MongoTemplate mongoTemplate;

    public MongoOAuth2ClientTokenRepositoryImpl(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean deleteByAuthenticationId(final String authenticationId) {
        final Query query = Query.query(Criteria.where("authenticationId").is(authenticationId));
        final DeleteResult deleteResult = mongoTemplate.remove(query, MongoOAuth2ClientToken.class);
        return deleteResult.wasAcknowledged();
    }

    @Override
    public MongoOAuth2ClientToken findByAuthenticationId(final String authenticationId) {
        final Query query = Query.query(Criteria.where("authenticationId").is(authenticationId));
        return mongoTemplate.findOne(query, MongoOAuth2ClientToken.class);
    }
}

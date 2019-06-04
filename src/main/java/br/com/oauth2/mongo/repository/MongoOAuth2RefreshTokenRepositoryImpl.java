package br.com.oauth2.mongo.repository;

import com.mongodb.client.result.DeleteResult;

import br.com.oauth2.mongo.entity.mongo.MongoOAuth2RefreshToken;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class MongoOAuth2RefreshTokenRepositoryImpl implements MongoOAuth2RefreshTokenRepositoryBase {

    public static final String ID = "_id";
    private MongoTemplate mongoTemplate;

    public MongoOAuth2RefreshTokenRepositoryImpl(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public MongoOAuth2RefreshToken findByTokenId(final String tokenId) {
        final Query query = Query.query(Criteria.where(ID).is(tokenId));
        return mongoTemplate.findOne(query, MongoOAuth2RefreshToken.class);
    }

    @Override
    public boolean deleteByTokenId(final String tokenId) {
        final Query query = Query.query(Criteria.where(ID).is(tokenId));
        final DeleteResult deleteResult = mongoTemplate.remove(query, MongoOAuth2RefreshToken.class);
        return deleteResult.wasAcknowledged();
    }
}

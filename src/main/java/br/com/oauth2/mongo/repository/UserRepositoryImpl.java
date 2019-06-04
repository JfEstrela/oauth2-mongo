package br.com.oauth2.mongo.repository;

import com.mongodb.client.result.UpdateResult;

import br.com.oauth2.mongo.entity.mongo.User;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Update.update;

@Repository
public class UserRepositoryImpl implements UserRepositoryBase {

    private final MongoTemplate mongoTemplate;

    public UserRepositoryImpl(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean changePassword(final String oldPassword,
                                  final String newPassword,
                                  final String username) {
        final Query searchUserQuery = new Query(where("username").is(username).andOperator(where("password").is(oldPassword)));
        final UpdateResult updateResult = mongoTemplate.updateFirst(searchUserQuery, update("password", newPassword), User.class);
        return updateResult.wasAcknowledged();
    }
}

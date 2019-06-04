package br.com.oauth2.mongo.repository;

import org.springframework.stereotype.Repository;

import br.com.oauth2.mongo.entity.mongo.MongoClientDetails;
@Repository
public interface MongoClientDetailsRepositoryBase {
    boolean deleteByClientId(String clientId);

    boolean update(MongoClientDetails mongoClientDetails);

    boolean updateClientSecret(String clientId, String newSecret);

    MongoClientDetails findByClientId(String clientId);
}

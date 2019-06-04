package br.com.oauth2.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.oauth2.mongo.entity.mongo.MongoClientDetails;
@Repository
public interface MongoClientDetailsRepository extends MongoRepository<MongoClientDetails, String>, MongoClientDetailsRepositoryBase {
}

package br.com.oauth2.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.oauth2.mongo.entity.mongo.User;

import java.util.Optional;
@Repository
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryBase {

    void deleteByUsername(String username);

    Optional<User> findByUsername(String username);

}

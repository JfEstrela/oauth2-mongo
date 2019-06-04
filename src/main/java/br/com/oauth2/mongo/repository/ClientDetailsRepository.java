package br.com.oauth2.mongo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.oauth2.mongo.entity.User;


@Repository
public interface ClientDetailsRepository extends JpaRepository<User, Long>{

	Optional<User> findByUsername(String username);

}

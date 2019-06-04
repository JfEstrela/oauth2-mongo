package br.com.oauth2.mongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.client.token.ClientKeyGenerator;
import org.springframework.security.oauth2.client.token.DefaultClientKeyGenerator;

@SpringBootApplication
public class Oauth2MongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(Oauth2MongoApplication.class, args);
	}
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

	@Bean
	public AuthenticationKeyGenerator authenticationKeyGenerator() {
		return new DefaultAuthenticationKeyGenerator();
	} 
	
	@Bean
	public ClientKeyGenerator ClientKeyGenerator() {
		return new DefaultClientKeyGenerator();
	}

}

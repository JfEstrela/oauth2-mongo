package br.com.oauth2.mongo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.ClientKeyGenerator;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.stereotype.Component;

import br.com.oauth2.mongo.entity.mongo.MongoOAuth2ClientToken;
import br.com.oauth2.mongo.repository.MongoOAuth2ClientTokenRepository;

import java.util.UUID;

@Component
public class MongoClientTokenServices implements ClientTokenServices {

	@Autowired
    private  MongoOAuth2ClientTokenRepository mongoOAuth2ClientTokenRepository;
	@Autowired
    private  ClientKeyGenerator clientKeyGenerator;

    public MongoClientTokenServices() {
    }

    @Override
    public OAuth2AccessToken getAccessToken(final OAuth2ProtectedResourceDetails resource,
                                            final Authentication authentication) {
        final MongoOAuth2ClientToken mongoOAuth2ClientToken = mongoOAuth2ClientTokenRepository.findByAuthenticationId(clientKeyGenerator.extractKey(resource, authentication));
        return SerializationUtils.deserialize(mongoOAuth2ClientToken.getToken());
    }

    @Override
    public void saveAccessToken(final OAuth2ProtectedResourceDetails resource,
                                final Authentication authentication,
                                final OAuth2AccessToken accessToken) {
        removeAccessToken(resource, authentication);
        final MongoOAuth2ClientToken mongoOAuth2ClientToken = new MongoOAuth2ClientToken(UUID.randomUUID().toString(),
                accessToken.getValue(),
                SerializationUtils.serialize(accessToken),
                clientKeyGenerator.extractKey(resource, authentication),
                authentication.getName(),
                resource.getClientId());

        mongoOAuth2ClientTokenRepository.save(mongoOAuth2ClientToken);
    }

    @Override
    public void removeAccessToken(final OAuth2ProtectedResourceDetails resource,
                                  final Authentication authentication) {
        mongoOAuth2ClientTokenRepository.deleteByAuthenticationId(clientKeyGenerator.extractKey(resource, authentication));
    }
}

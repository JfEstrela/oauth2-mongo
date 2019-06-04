package br.com.oauth2.mongo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.*;
import org.springframework.stereotype.Service;
import br.com.oauth2.mongo.config.ClienteDetailServiceProperties;
import br.com.oauth2.mongo.entity.mongo.MongoClientDetails;

import br.com.oauth2.mongo.repository.MongoClientDetailsRepository;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

@Service
public class MongoClientDetailsService implements ClientDetailsService, ClientRegistrationService {

    private final MongoClientDetailsRepository mongoClientDetailsRepository;
    
    @Autowired
    private  PasswordEncoder passwordEncoder;
    
    @Autowired
    private ClienteDetailServiceProperties clienteDetailServiceProperties;
    
    @PostConstruct
    private void createUserService() {
    	removeClientDetails(clienteDetailServiceProperties.getClientId());
    	MongoClientDetails user = new MongoClientDetails(clienteDetailServiceProperties.getClientId(),
    			clienteDetailServiceProperties.getClientSecret(),
    			scopes(),
    			null, 
    			authorizedGrantTypes(), 
    			null,
    			getAuthorities(), 
    			clienteDetailServiceProperties.getTokenValiditySeconds(),
    			clienteDetailServiceProperties.getTokenValiditySeconds(), 
    			null, null);
    	addClientDetails(user);
    }

    public MongoClientDetailsService(final MongoClientDetailsRepository mongoClientDetailsRepository,
                                     final PasswordEncoder passwordEncoder) {
        this.mongoClientDetailsRepository = mongoClientDetailsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ClientDetails loadClientByClientId(final String clientId) {
        try {
        	return mongoClientDetailsRepository.findByClientId(clientId);
        } catch (IllegalArgumentException e) {
            throw new ClientRegistrationException("No Client Details for client id", e);
        }
    }

    @Override
    public void addClientDetails(final ClientDetails clientDetails) {
        final MongoClientDetails mongoClientDetails = new MongoClientDetails(clientDetails.getClientId(),
                passwordEncoder.encode(clientDetails.getClientSecret()),
                clientDetails.getScope(),
                clientDetails.getResourceIds(),
                clientDetails.getAuthorizedGrantTypes(),
                clientDetails.getRegisteredRedirectUri(),
                newArrayList(clientDetails.getAuthorities()),
                clientDetails.getAccessTokenValiditySeconds(),
                clientDetails.getRefreshTokenValiditySeconds(),
                clientDetails.getAdditionalInformation(),
                getAutoApproveScopes(clientDetails));

        mongoClientDetailsRepository.save(mongoClientDetails);
    }

    @Override
    public void updateClientDetails(final ClientDetails clientDetails) {
        final MongoClientDetails mongoClientDetails = new MongoClientDetails(clientDetails.getClientId(),
                clientDetails.getClientSecret(),
                clientDetails.getScope(),
                clientDetails.getResourceIds(),
                clientDetails.getAuthorizedGrantTypes(),
                clientDetails.getRegisteredRedirectUri(),
                newArrayList(clientDetails.getAuthorities()),
                clientDetails.getAccessTokenValiditySeconds(),
                clientDetails.getRefreshTokenValiditySeconds(),
                clientDetails.getAdditionalInformation(),
                getAutoApproveScopes(clientDetails));
        final boolean result = mongoClientDetailsRepository.update(mongoClientDetails);

        if (!result) {
            throw new NoSuchClientException("No such Client Id");
        }
    }

    @Override
    public void updateClientSecret(final String clientId,
                                   final String secret) {
        final boolean result = mongoClientDetailsRepository.updateClientSecret(clientId, passwordEncoder.encode(secret));
        if (!result) {
            throw new NoSuchClientException("No such client id");
        }
    }

    @Override
    public void removeClientDetails(String clientId) {
        final boolean result = mongoClientDetailsRepository.deleteByClientId(clientId);
        if (!result) {
            throw new NoSuchClientException("No such client id");
        }
    }

    @Override
    public List<ClientDetails> listClientDetails() {
        final List<MongoClientDetails> allClientDetails = mongoClientDetailsRepository.findAll();
        return newArrayList(allClientDetails);
    }

    private Set<String> getAutoApproveScopes(final ClientDetails clientDetails) {
        if (clientDetails.isAutoApprove("true")) {
            return newHashSet("true"); // all scopes autoapproved
        }

        return clientDetails.getScope().stream()
                .filter(clientDetails::isAutoApprove)
                .collect(Collectors.toSet());
    }
    
    private List<GrantedAuthority> getAuthorities() {
	    List<GrantedAuthority> authorities
	      = new ArrayList<GrantedAuthority>();
	    authorities.add(new SimpleGrantedAuthority("ROLE_SERVICE"));	     
	    return authorities;
	}
    
    private Set<String> scopes(){
    	return new HashSet<>(Arrays.asList( new String [] {"read","write","trust"}));
    }
    
    private Set<String> authorizedGrantTypes(){
    	return new HashSet<>(Arrays.asList( new String [] {"password","client_credentials", "refresh_token"}));
    }
}

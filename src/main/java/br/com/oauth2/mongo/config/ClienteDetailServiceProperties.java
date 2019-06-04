package br.com.oauth2.mongo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "oauth2.mongo.authorization")
@Component
public class ClienteDetailServiceProperties {
	
	private String clientId;
	private String clientSecret;
	private    int tokenValiditySeconds;
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	public int getTokenValiditySeconds() {
		return tokenValiditySeconds;
	}
	public void setTokenValiditySeconds(int tokenValiditySeconds) {
		this.tokenValiditySeconds = tokenValiditySeconds;
	}

	

}

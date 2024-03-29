package br.com.oauth2.mongo.mongostore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import br.com.oauth2.mongo.entity.mongo.MongoOAuth2AccessToken;
import br.com.oauth2.mongo.entity.mongo.MongoOAuth2RefreshToken;
import br.com.oauth2.mongo.repository.MongoOAuth2AccessTokenRepository;
import br.com.oauth2.mongo.repository.MongoOAuth2RefreshTokenRepository;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.springframework.security.oauth2.common.util.SerializationUtils.deserialize;
import static org.springframework.security.oauth2.common.util.SerializationUtils.serialize;

@Component
public class MongoTokenStore implements TokenStore {

	@Autowired
    private  MongoOAuth2AccessTokenRepository mongoOAuth2AccessTokenRepository;
	@Autowired
    private  MongoOAuth2RefreshTokenRepository mongoOAuth2RefreshTokenRepository;
	@Autowired
    private  AuthenticationKeyGenerator authenticationKeyGenerator;

    public MongoTokenStore() {
     
    }

    @Override
    public OAuth2Authentication readAuthentication(final OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(final String token) {
        final String tokenId = extractTokenKey(token);

        final MongoOAuth2AccessToken mongoOAuth2AccessToken = mongoOAuth2AccessTokenRepository.findByTokenId(tokenId);

        if (nonNull(mongoOAuth2AccessToken)) {
            try {
                return deserializeAuthentication(mongoOAuth2AccessToken.getAuthentication());
            } catch (IllegalArgumentException e) {
                removeAccessToken(token);
            }
        }

        return null;
    }

    @Override
    public void storeAccessToken(final OAuth2AccessToken token,
                                 final OAuth2Authentication authentication) {
        String refreshToken = null;
        if (nonNull(token.getRefreshToken())) {
            refreshToken = token.getRefreshToken().getValue();
        }

        if (nonNull(readAccessToken(token.getValue()))) {
            removeAccessToken(token.getValue());
        }

        final String tokenKey = extractTokenKey(token.getValue());

        final MongoOAuth2AccessToken oAuth2AccessToken = new MongoOAuth2AccessToken(tokenKey,
                serializeAccessToken(token),
                authenticationKeyGenerator.extractKey(authentication),
                authentication.isClientOnly() ? null : authentication.getName(),
                authentication.getOAuth2Request().getClientId(),
                serializeAuthentication(authentication),
                extractTokenKey(refreshToken));

        mongoOAuth2AccessTokenRepository.save(oAuth2AccessToken);
    }

    @Override
    public OAuth2AccessToken readAccessToken(final String tokenValue) {
        final String tokenKey = extractTokenKey(tokenValue);
        final MongoOAuth2AccessToken mongoOAuth2AccessToken = mongoOAuth2AccessTokenRepository.findByTokenId(tokenKey);
        if (nonNull(mongoOAuth2AccessToken)) {
            try {
                return deserializeAccessToken(mongoOAuth2AccessToken.getToken());
            } catch (IllegalArgumentException e) {
                removeAccessToken(tokenValue);
            }
        }
        return null;
    }

    @Override
    public void removeAccessToken(final OAuth2AccessToken token) {
        removeAccessToken(token.getValue());
    }

    @Override
    public void storeRefreshToken(final OAuth2RefreshToken refreshToken,
                                  final OAuth2Authentication oAuth2Authentication) {
        final String tokenKey = extractTokenKey(refreshToken.getValue());
        final byte[] token = serializeRefreshToken(refreshToken);
        final byte[] authentication = serializeAuthentication(oAuth2Authentication);

        final MongoOAuth2RefreshToken oAuth2RefreshToken = new MongoOAuth2RefreshToken(tokenKey, token, authentication);

        mongoOAuth2RefreshTokenRepository.save(oAuth2RefreshToken);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(final String tokenValue) {
        final String tokenKey = extractTokenKey(tokenValue);
        final MongoOAuth2RefreshToken mongoOAuth2RefreshToken = mongoOAuth2RefreshTokenRepository.findByTokenId(tokenKey);

        if (nonNull(mongoOAuth2RefreshToken)) {
            try {
                return deserializeRefreshToken(mongoOAuth2RefreshToken.getToken());
            } catch (IllegalArgumentException e) {
                removeRefreshToken(tokenValue);
            }
        }

        return null;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(final OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    @Override
    public void removeRefreshToken(final OAuth2RefreshToken token) {
        removeRefreshToken(token.getValue());
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(final OAuth2RefreshToken refreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    @Override
    public OAuth2AccessToken getAccessToken(final OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = null;

        String key = authenticationKeyGenerator.extractKey(authentication);

        final MongoOAuth2AccessToken oAuth2AccessToken = mongoOAuth2AccessTokenRepository.findByAuthenticationId(key);

        if (oAuth2AccessToken != null) {
            accessToken = deserializeAccessToken(oAuth2AccessToken.getToken());
        }

        if (accessToken != null
                && !key.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
            removeAccessToken(accessToken.getValue());
            // Keep the store consistent (maybe the same user is represented by this authentication but the details have
            // changed)
            storeAccessToken(accessToken, authentication);
        }
        return accessToken;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        final List<MongoOAuth2AccessToken> oAuth2AccessTokens = mongoOAuth2AccessTokenRepository.findByUsernameAndClientId(userName, clientId);
        return transformToOAuth2AccessTokens(oAuth2AccessTokens);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(final String clientId) {
        final List<MongoOAuth2AccessToken> oAuth2AccessTokens = mongoOAuth2AccessTokenRepository.findByClientId(clientId);
        return transformToOAuth2AccessTokens(oAuth2AccessTokens);
    }

    protected String extractTokenKey(final String value) {
        if (Objects.isNull(value)) {
            return null;
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
        }

        try {
            byte[] bytes = digest.digest(value.getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, bytes));
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
        }
    }

    protected byte[] serializeAccessToken(final OAuth2AccessToken token) {
        return serialize(token);
    }

    protected byte[] serializeRefreshToken(final OAuth2RefreshToken token) {
        return serialize(token);
    }

    protected byte[] serializeAuthentication(final OAuth2Authentication authentication) {
        return serialize(authentication);
    }

    protected OAuth2AccessToken deserializeAccessToken(final byte[] token) {
        return deserialize(token);
    }

    protected OAuth2RefreshToken deserializeRefreshToken(final byte[] token) {
        return deserialize(token);
    }

    protected OAuth2Authentication deserializeAuthentication(final byte[] authentication) {
        return deserialize(authentication);
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(final String value) {
        final String tokenId = extractTokenKey(value);

        final MongoOAuth2RefreshToken mongoOAuth2RefreshToken = mongoOAuth2RefreshTokenRepository.findByTokenId(tokenId);

        if (nonNull(mongoOAuth2RefreshToken)) {
            try {
                return deserializeAuthentication(mongoOAuth2RefreshToken.getAuthentication());
            } catch (IllegalArgumentException e) {
                removeRefreshToken(value);
            }
        }

        return null;
    }

    private void removeRefreshToken(final String token) {
        final String tokenId = extractTokenKey(token);
        mongoOAuth2RefreshTokenRepository.deleteByTokenId(tokenId);
    }

    private void removeAccessTokenUsingRefreshToken(final String refreshToken) {
        final String tokenId = extractTokenKey(refreshToken);
        mongoOAuth2AccessTokenRepository.deleteByRefreshTokenId(tokenId);

    }

    private void removeAccessToken(final String tokenValue) {
        final String tokenKey = extractTokenKey(tokenValue);
        mongoOAuth2AccessTokenRepository.deleteByTokenId(tokenKey);
    }

    private Collection<OAuth2AccessToken> transformToOAuth2AccessTokens(final List<MongoOAuth2AccessToken> oAuth2AccessTokens) {
        return oAuth2AccessTokens.stream()
                .filter(Objects::nonNull)
                .map(token -> SerializationUtils.<OAuth2AccessToken>deserialize(token.getToken()))
                .collect(Collectors.toList());
    }
}

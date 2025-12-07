package com.woh.udp.services;

import com.woh.udp.Util.RedisCacheStore;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JWTService {
    @Value("${udp.expiresin}")
    private Long EXPIRES_IN;
    @Value("${udp.redishash}")
    private String REDIS_HASH;

    private final RedisCacheStore redisCacheStore;

    public String generateJwtToken(UUID id) throws NoSuchAlgorithmException, NoSuchProviderException {
        return Jwts.builder()
                .subject(id.toString())
                .issuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis()+EXPIRES_IN))
                .signWith(SignatureAlgorithm.HS256,(String) redisCacheStore.get(REDIS_HASH))
                .compact();
    }

    public String getUserIdFromJwt(String jwtToken){
        return Jwts.parser()
                .setSigningKey((String) redisCacheStore.get(REDIS_HASH))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody()
                .getSubject()
                .toString();
    }

    public boolean isJwtValid(String jwtToken){
        try {
            Claims claims =  Jwts.parser().setSigningKey((String) redisCacheStore.get(REDIS_HASH)).build().parseClaimsJws(jwtToken).getBody();
            return !isJwtExpired(claims);
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean isJwtExpired(Claims claims){
        return claims.getExpiration().before(new Date(System.currentTimeMillis()));
    }
}
package com.racha.ChatWithMe.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    private final JwtEncoder encoder;

    // @Value("${token.expiration-hours:4}")
    private long expirationHours = 4;

    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generateToken(Authentication authentication) {
        try {
            Instant now = Instant.now();
            JwtClaimsSet claims = buildClaims(authentication, now);
            String token = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
            logger.info("Generated token for user: {}", authentication.getName());
            return token;
        } catch (Exception e) {
            logger.error("Failed to generate token for user: {}", authentication.getName(), e);
            throw new RuntimeException("Token generation failed", e);
        }
    }

    private JwtClaimsSet buildClaims(Authentication authentication, Instant now) {
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        return JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(expirationHours, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
    }
}

package com.appcenter.monolithicwnt.auth.infrastructure;

import com.appcenter.monolithicwnt.auth.application.JwtManager;
import com.appcenter.monolithicwnt.global.exception.BusinessException;
import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import com.appcenter.monolithicwnt.user.dto.request.Authentication;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.SIG.HS256;

@Component
public class JwtTokenManager implements JwtManager {

    private final int expiration;
    private final String secretKey;


    public JwtTokenManager(@Value("${jwt.expiration.time}") int expiration,
                           @Value("${jwt.secret.key}") String secretKey) {
        this.expiration = expiration;
        this.secretKey = secretKey;
    }

    @Override
    public String createToken(Long id, String email) {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");

        return Jwts.builder()
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .subject(id.toString())
                .claim("email", email)
                .issuedAt(new Date())
                .signWith(key,HS256)
                .compact();
    }

    @Override
    public Claims extractClaims(String token) {
        return handleJwtException(token, (value) ->
                Jwts.parser()
                        .verifyWith(new SecretKeySpec(secretKey.getBytes(),"HmacSHA256"))
                        .build()
                        .parseSignedClaims(value).getPayload()
        );
    }

    @Override
    public Authentication extractAuthentication(String token) {
        Long id = handleJwtException(token, this::extractIdFromToken);

        return Authentication.from(id);
    }

    private Long extractIdFromToken(String token) {
        Jws<Claims> verifiedJwt = Jwts.parser()
                .verifyWith(new SecretKeySpec(secretKey.getBytes(), "HmacSHA256"))
                .build()
                .parseSignedClaims(token);
        return Long.valueOf(verifiedJwt.getPayload().getSubject());
    }

    private <T> T handleJwtException(String token, Function<String, T> function){
        try{
            return function.apply(token);
        }catch (MalformedJwtException malformedJwtException){
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }catch (ExpiredJwtException expiredJwtException){
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }catch (IllegalArgumentException illegalArgumentException){
            throw new BusinessException(ErrorCode.TOKEN_EMPTY);
        }catch (SignatureException signatureException){
            throw new BusinessException(ErrorCode.TOKEN_NOT_SIGNED);
        }catch (JwtException jwtException){
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

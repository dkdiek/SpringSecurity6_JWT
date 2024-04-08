package com.example.demo.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {
    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    //username
    public String getUsername(String token) {

        /*payload에 들어갈 정보들은 claim이라 부릅니다. 이 claim들은 크게 세 종류로 나뉩니다.

        등록된 클레임 목록
        iss : 이 데이터의 발행자를 뜻합니다.
        iat : 이 데이터가 발행된 시간을 뜻합니다.
        exp : 이 데이터가 만료된 시간을 뜻합니다.
        sub : 토큰의 제목입니다.
        aud : 토큰의 대상입니다.
        nbf : 토큰이 처리되지 않아야 할 시점을 의미합니다.
        이 시점이 지나기 전엔 토큰이 처리되지 않습니다.
        jti : 토큰의 고유 식별자입니다.

        두 번째는 공개 클레임(public claims)입니다. 사용자 마음대로 쓸 수 있으나 충돌 방지를 위해 여기에 정의된대로 사용하는 게 좋습니다. 그렇지 않다면 URI 형식으로 키를 정해야 합니다.

        세 번째는 비공개(private claims)입니다. 통신을 주고받는 당사자들끼리 협의해서 자유롭게 키와 값을 정할 수 있습니다.*/

        //token이 우리 서버에서 생성됬는지 검증(header.payload.signature)
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    //Role
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    //isExpired
    public Boolean isExpired(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    //create JWT
    public String createJwt(String username, String role, Long expiredMs){
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))//만료
                .signWith(secretKey)//secretkey를 통해 암호화 진행
                .compact();

    }

}

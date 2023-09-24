package springboot.study.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import springboot.study.domain.User;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    private String makeToken(Date expiry, User user) { //토큰 생성 메서드
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) //헤더 타입: jwt
                .setIssuer(jwtProperties.getIssuer()) //내용 iss: application.yml에서 설정한 issuer 값
                .setIssuedAt(now) //내용 iat: 현재 시간
                .setExpiration(expiry) //내용 exp: expiry 멤버 변수값
                .setSubject(user.getEmail()) //내용 sub: 유저의 이메일
                .claim("id", user.getId()) //클레임 id: 유저 아이디
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) //서명: secretkey와 함께 해시값 암호화
                .compact();
    }

    //jwt 토큰 유효성 검증 메소드
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) //암호 복호화
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) { // 복호화 에러시 유효하지 x
            return false;
        }
    }


    public Authentication getAuthentication(String token) { //토큰 기반 인증 정보 가져오는 메소드
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject
                (), "", authorities), token, authorities);
    }

    //토큰 기반 id 가져오는 메소드
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) { //클레임 조회
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}

package springboot.study.config.jwt;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import springboot.study.domain.User;
import springboot.study.repository.UserRepository;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        // given 토큰에 유저 정보를 추가하기 위한 테스트 유저 생성
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        // when 토큰 생성
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        // then 토큰 복호화, 클레임의 id 값이 testUser의 id와 같은지 확인
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }

    @DisplayName("validToken(): 만료된 토큰인 경우에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        // given jwt라이브러리를 통해 토큰 생성. 이미 만료된 토큰으로 생성함
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        // when 유효한 토큰인지 검증
        boolean result = tokenProvider.validToken(token);

        // then 만료되었는지 확인
        assertThat(result).isFalse();
    }


    @DisplayName("validToken(): 유효한 토큰인 경우에 유효성 검증에 성공한다.")
    @Test
    void validToken_validToken() {
        // given 토큰 생성. 만료 안된 토큰으로 생성
        String token = JwtFactory.withDefaultValues()
                .createToken(jwtProperties);

        // when 유효한 토큰인지 검증
        boolean result = tokenProvider.validToken(token);

        // then 유효한지 확인
        assertThat(result).isTrue();
    }


    @DisplayName("getAuthentication(): 토큰 기반으로 인증정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        // given 토큰 생성(subject는 이메일값으로 넣음)
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        // when 인증 객체 반환
        Authentication authentication = tokenProvider.getAuthentication(token);

        // then 반환받은 인증 객체의 유저정보와 토큰 생성시의 유저 정보 일치 확인
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    @DisplayName("getUserId(): 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId() {
        // given 토큰 생성(클레임에 key, 유저 id 추가)
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        // when 유저 id 반환
        Long userIdByToken = tokenProvider.getUserId(token);

        // then 토큰 생성시 설정한 유저 id와 값이 같은지 확인
        assertThat(userIdByToken).isEqualTo(userId);
    }
}
package springboot.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import springboot.study.config.jwt.TokenProvider;
import springboot.study.domain.User;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken){
        //토큰 유효성 검사 실패하면 예외 발생
        if(!tokenProvider.validToken(refreshToken)){
            throw new IllegalArgumentException("Unexpected token");
        }

        //리프레시 토큰으로 id를 찾고, id로 사용자를 찾음
        Long userId=refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user=userService.findById(userId);

        //새로운 액세스 토큰 생성
        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}

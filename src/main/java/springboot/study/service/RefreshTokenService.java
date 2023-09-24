package springboot.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import springboot.study.domain.RefreshToken;
import springboot.study.repository.RefreshTokenRepository;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    //리프레시 토큰 객체를 검색해서 전달하는 메소드
    public RefreshToken findByRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new IllegalArgumentException("Unexpected token"));
    }
}

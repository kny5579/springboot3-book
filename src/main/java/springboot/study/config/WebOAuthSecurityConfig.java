package springboot.study.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import springboot.study.config.jwt.TokenProvider;
import springboot.study.config.oauth.OAuth2UserCustomService;
import springboot.study.repository.RefreshTokenRepository;
import springboot.study.service.UserService;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer configure(){ //스프링 시큐리티 기능 비활성화

    }

}

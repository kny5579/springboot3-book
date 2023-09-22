package springboot.study.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import springboot.study.service.UserDetailService;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailService userService;

    @Bean
    public WebSecurityCustomizer configure() { //스프링 시큐리티의 인증, 인가 서비스를 모든 곳에 적용하는 것이 아니라 특정 부분에는 적용하지 않음
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/static/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { //특정 http 요청에 대한 웹 기반 보안 구성
        return http
                .authorizeRequests() //인증, 인가 설정
                .requestMatchers("/login", "/signup", "/user").permitAll() //이 url로 요청이 오면 인증/인가 없이 접근가능
                .anyRequest().authenticated()//위의 url 이외의 요청은 권한 필요
                .and()
                .formLogin() //폼 기반 로그인 설정
                .loginPage("/login")
                .defaultSuccessUrl("/articles") //로그인 성공시 이동하는 url
                .and()
                .logout() //로그아웃 설정
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true) //로그아웃 이후 세션 전체 삭제 여부
                .and()
                .csrf().disable() //csrf 비활성화(csrf 공격 방지를 위해서는 활성화하는것이 좋지만 실습의 편의를 위함)
                .build();
    }

    //인증 관리자 관련 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService) //사용자 정보 서비스 설정
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { //패스워드 암호화
        return new BCryptPasswordEncoder();
    }
}
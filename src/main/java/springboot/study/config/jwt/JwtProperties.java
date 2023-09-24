package springboot.study.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("jwt") //application.yml에 적은 jwt 프로퍼티 값을 가져와서 사용하는 어노테이션
public class JwtProperties { //application.yml에 적은 값들을 변수로 접근하는데 사용할 클래스
    private String issuer;
    private String secretKey;
}

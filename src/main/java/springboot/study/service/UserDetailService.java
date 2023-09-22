package springboot.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import springboot.study.domain.User;
import springboot.study.repository.UserRepository;

@RequiredArgsConstructor
@Service
//스프링 시큐리티에서 사용자 정보를 가져옴
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String email){ //사용자 이름(이메일)으로 사용자 정보를 가져옴
        return userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalArgumentException((email)));
    }
}

package springboot.study.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name="users")
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Getter
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", updatable = false)
    private Long id;

    @Column(name="email",nullable = false,unique = true)
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="nickname",unique = true)
    private String nickname;

    @Builder
    public User(String email, String password, String nickname){
        this.email=email;
        this.password=password;
        this.nickname=nickname;
    }

    //사용자 이름 변경하는 경우
    public User update(String nickname){
        this.nickname=nickname;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){ //권한 반환
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getUsername(){
        return email;
    }

    @Override
    public String getPassword(){
        return password;
    }

    @Override
    public boolean isAccountNonExpired(){ //계정 만료 여부 반환
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){ //계정 잠금 되었는지 확인
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){ //패스워드의 만료 여부 반환
        return true;
    }

    @Override
    public boolean isEnabled(){ //계정 사용 가능 여부 반환
        return true;
    }


}
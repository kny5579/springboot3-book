package springboot.study.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//회원가입, 로그인 뷰 컨트롤러
@Controller
public class UserViewController {
    @GetMapping("/login")
    public String login(){
        return "login"; //login.html 반환
    }

    @GetMapping("/signup")
    public String signup(){
        return "signup"; //signup.html 반환
    }
}

package com.shop.config;

import com.shop.service.MemberService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@Log4j2
public class SecurityConfig {

    @Autowired
    MemberService memberService;

    // 패스워드 인코더 빈 설정
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    // SecurityFilterChain을 빌드하는 빈 설정
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        log.info("--------------------securityFilterChain-----------------------------");

        // 로그인 설정
        http.formLogin()
                .loginPage("/members/login") // 로그인 페이지 경로 설정
                .defaultSuccessUrl("/") // 로그인 성공 후 이동할 경로 설정
//                login폼에서 name을 username 대신 사용할 때 변경된 이름을 기입한다.
                .usernameParameter("email")  // 로그인 폼에서 사용할 파라미터 이름 설정
                .failureUrl("/members/login/error") // 로그인 실패 시 이동할 경로 설정
                .and()// 로그아웃 설정
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/"); // 로그아웃 성공 후 이동할 경로 설정

        // 요청 권한 설정
        http.authorizeRequests()
                .mvcMatchers("/css/**", "/js/**", "/img/**").permitAll()  // 정적 리소스에 대한 접근 권한 설정
                .mvcMatchers("/", "/members/**", "/item/**", "/images/**").permitAll() // 특정 경로에 대한 접근 권한 설정
                .mvcMatchers("/admin/**").hasRole("ADMIN") // 특정 역할이 있는 사용자에 대한 접근 권한 설정
                .anyRequest().authenticated(); // 그 외의 요청은 인증된 사용자만 접근 가능하도록 설정

        // CSRF 비활성화
        http.csrf().disable();

        return http.build();
    }

}

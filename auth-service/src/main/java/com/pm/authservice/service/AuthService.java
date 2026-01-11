package com.pm.authservice.service;


import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.model.User;
import com.pm.authservice.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;




public Optional<String> authenticate(LoginRequestDTO loginRequestDTO){

    Optional<String> token=userService.findByEmail(loginRequestDTO.getEmail())
            .filter(u->passwordEncoder.matches(loginRequestDTO.getPassword(),u.getPassword()))
            .map(u->jwtUtil.generateToken(u.getEmail(),u.getRole()));

    return token;
}

public Boolean validateToken(String token){

    try {
jwtUtil.validateToken(token);
        return true;
    }
    catch (JwtException e){
        return false;
    }
}

}

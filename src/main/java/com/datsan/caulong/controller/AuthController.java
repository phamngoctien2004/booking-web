package com.datsan.caulong.controller;

import com.datsan.caulong.dto.request.LoginRequest;
import com.datsan.caulong.dto.request.RegisterRequest;
import com.datsan.caulong.dto.response.ApiResponse;
import com.datsan.caulong.dto.response.LoginResponse;
import com.datsan.caulong.repository.UserRepository;
import com.datsan.caulong.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        ApiResponse<LoginResponse> response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest){
        ApiResponse<?> response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        String jwt = request.getHeader("Authorization").substring(7);
        authService.logout(jwt);
        return ResponseEntity.ok("Đăng xuất thành công");
    }

}

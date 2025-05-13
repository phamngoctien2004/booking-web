package com.datsan.caulong.controller;

import com.datsan.caulong.dto.request.LoginRequest;
import com.datsan.caulong.dto.request.RegisterRequest;
import com.datsan.caulong.dto.response.ApiResponse;
import com.datsan.caulong.dto.response.LoginResponse;
import com.datsan.caulong.repository.UserRepository;
import com.datsan.caulong.service.AuthService;
import com.datsan.caulong.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        ApiResponse<LoginResponse> response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) throws MessagingException {
        ApiResponse<?> response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        String jwt = request.getHeader("Authorization").substring(7);
        authService.logout(jwt);
        return ResponseEntity.ok("Đăng xuất thành công");
    }

    @GetMapping("/verify")
    public ResponseEntity<?> email(@RequestParam("email") String email, @RequestParam("token") String token){
        authService.verifyEmail(email,token);
        return ResponseEntity.ok("Xác thực email thành công");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> reset(@RequestBody LoginRequest loginRequest) throws MessagingException {
        authService.resetPassword(loginRequest.getEmail());
        return ResponseEntity.ok("Lấy lại mật khẩu thành công - Kiểm tra email của bạn");
    }
}

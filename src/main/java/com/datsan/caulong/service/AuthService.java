package com.datsan.caulong.service;

import com.datsan.caulong.dto.request.LoginRequest;
import com.datsan.caulong.dto.request.RegisterRequest;
import com.datsan.caulong.dto.response.ApiResponse;
import com.datsan.caulong.dto.response.LoginResponse;
import com.datsan.caulong.exception.AppException;
import com.datsan.caulong.exception.Error;
import com.datsan.caulong.model.Token;
import com.datsan.caulong.model.Role;
import com.datsan.caulong.model.User;
import com.datsan.caulong.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    @Value("${jwt.secret}")
    public String secretKey;

    @Value("${jwt.expiration}")
    public int expiration;
    private final UserService userService;
    private final RoleService roleService;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;
    public AuthService(UserService userService,
                       RoleService roleService,
                       TokenRepository tokenRepository,
                       EmailService emailService){
        this.userService = userService;
        this.roleService = roleService;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }


    public ApiResponse<LoginResponse> login(LoginRequest loginRequest){
        // kiểm tra tài khoản mật khẩu
        User user = this.userService.findByEmail(loginRequest.getEmail());

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean isMatchesPassword = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());

        if(!isMatchesPassword){
            throw new AppException(Error.PASSWORD_INCORRECT);
        }
        if(!user.isActive()){
            throw new AppException(Error.USER_UNACTiVED);
        }

        // tạo token
        String jwt = generateJwt(user);
        user.setPassword(null);

        return ApiResponse.<LoginResponse>builder()
                .status("success")
                .message("Đăng nhập thành công")
                .data(new LoginResponse(jwt, user))
                .build();
    }

    public ApiResponse<?> register(RegisterRequest registerRequest) throws MessagingException {
        // kiểm tra email tồn tại
        Optional<User> user = userService.OFindByEmail(registerRequest.getEmail());
        if(user.isPresent()){
            throw new AppException(Error.USER_EXISTED);
        }
        Role defaultRole = roleService.findRoleByName("user");
        User newUser = User.builder()
                .email(registerRequest.getEmail())
                .password(new BCryptPasswordEncoder().encode(registerRequest.getPassword()))
                .role(defaultRole)
                .build();
        userService.save(newUser);

//       Tạo UUUID xác thực email
        String emailVerifyToken = UUID.randomUUID().toString();
        Token token = new Token();
        token.setId(emailVerifyToken);
        token.setType("email");
        token.setValid(true);
        token.setEmail(newUser.getEmail());
        tokenRepository.save(token);

        String html = "<h3>Xin chào</h3>"
                + "<p>Click vào <a href='http://localhost:8080/v1/auth/verify?email=" + newUser.getEmail()
                + "&token=" + emailVerifyToken
                + "'>liên kết này</a> để xác minh tài khoản.</p>";


        emailService.sendEmail(newUser.getEmail(), html);

        return ApiResponse.builder()
                .status("success")
                .message("Đăng kí thành công")
                .build();
    }

    public void logout(String jwt){
        // lưu token vào blacklist
        Token token = new Token();
        token.setId(jwt);
        token.setType("jwt");
        token.setValid(false);
        tokenRepository.save(token);
    }
    public void verifyEmail(String email, String tokenRequest){
        Optional<Token> token = tokenRepository.findById(tokenRequest);

        if(token.isEmpty() || !token.get().getEmail().equals(email)){
            throw new AppException(Error.VERIFY_FAILED);
        }

        User user = userService.findByEmail(email);
        user.setActive(true);
        userService.save(user);

        tokenRepository.delete(token.get());
    }
    public void resetPassword(String email) throws MessagingException {
        User user = userService.findByEmail(email);
        // tạo mật khẩu mới 6 kí tự
        String newPassword = UUID.randomUUID().toString().substring(0,6);
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));

        userService.save(user);
        String html = "<h3>Xin chào</h3>"
                + "Mật khẩu mới của bạn là <b>"
                + newPassword +
                "</b> Vui lòng đăng nhập vào đổi mật khẩu để tránh rủi ro";
        emailService.sendEmail(email,html);
    }
    public String generateJwt(User user){
        Instant now = Instant.now();
        Instant expiry = now.plus(expiration, ChronoUnit.MINUTES);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .setSubject(user.getEmail())
                .claim("role", user.getRole().getName())
                .compact();
    }

    public  Claims extractClaims(String jwt){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                // tạo ra jwtParser và chọn method parseJWs để giải token có chữ kí
                .parseClaimsJws(jwt)
                .getBody();
    }
    public  SecretKey getSignKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
    public boolean isValidToken(String jwt){
        return !tokenRepository.existsById(jwt);
    }
}

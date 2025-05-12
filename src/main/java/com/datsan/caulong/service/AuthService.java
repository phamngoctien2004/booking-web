package com.datsan.caulong.service;

import com.datsan.caulong.dto.request.LoginRequest;
import com.datsan.caulong.dto.request.RegisterRequest;
import com.datsan.caulong.dto.response.ApiResponse;
import com.datsan.caulong.dto.response.LoginResponse;
import com.datsan.caulong.exception.AppException;
import com.datsan.caulong.exception.Error;
import com.datsan.caulong.model.InvalidToken;
import com.datsan.caulong.model.Role;
import com.datsan.caulong.model.User;
import com.datsan.caulong.repository.InvalidTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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
    private final InvalidTokenRepository invalidTokenRepository;
    public AuthService(UserService userService, RoleService roleService, InvalidTokenRepository invalidTokenRepository){
        this.userService = userService;
        this.roleService = roleService;
        this.invalidTokenRepository = invalidTokenRepository;
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

    public ApiResponse<?> register(RegisterRequest registerRequest){
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
        return ApiResponse.builder()
                .status("success")
                .message("Đăng kí thành công")
                .build();
    }

    public void logout(String jwt){
        // lưu token vào blacklist
        InvalidToken invalidToken = new InvalidToken();
        invalidToken.setId(jwt);
        invalidTokenRepository.save(invalidToken);
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
        return !invalidTokenRepository.existsById(jwt);
    }
}

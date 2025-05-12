package com.datsan.caulong.config;

import com.datsan.caulong.dto.response.ApiResponse;
import com.datsan.caulong.util.UserServiceImpl;
import com.datsan.caulong.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final UserServiceImpl userDetailsService;
    private final String[] WHITE_LIST = {
            "/v1/auth/login",
            "/v1/auth/register",
            "/v1/auth/email",
            "/v1/auth/reset"
    };
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String jwt = this.getToken(request);

            // bỏ qua lọc jwt
            if(jwt == null || this.checkWhiteList(request.getRequestURI())){
                filterChain.doFilter(request,response);
                return;
            }
            if(!authService.isValidToken(jwt)){
                throw new Exception();
            }
            // giải mã token và lấy email
            Claims claims = authService.extractClaims(jwt);
            String email = claims.getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // tạo đối tượng authentication
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(), null, userDetails.getAuthorities()
            );

            // lưu vào context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request,response);

        }catch(Exception ex ){
            handleResponse(response);
        }
    }

    private String getToken(HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        if(jwt == null){
           return null;
        }
        jwt = jwt.substring(7);
        return jwt;
    }
    private boolean checkWhiteList(String uri){
        for(String it: WHITE_LIST){
            if(it.equals(uri)){
                return true;
            }
        }
        return false;
    }
    private void handleResponse(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE  + ";charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectMapper objectMapper = new ObjectMapper();

        ApiResponse<?> responseBody = ApiResponse.builder()
                .status("Error")
                .message("Đã hết phiên làm việc vui lòng đăng nhập lại")
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}

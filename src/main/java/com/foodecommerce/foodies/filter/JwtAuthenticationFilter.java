package com.foodecommerce.foodies.filter;

import com.foodecommerce.foodies.service.FactUserDetailsService;
import com.foodecommerce.foodies.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor

/*Request → JwtAuthenticationFilter
    → Không có Bearer token → tiếp tục → Spring Security chặn nếu endpoint cần auth
    → Có token, validate thất bại → tiếp tục → Spring Security chặn (401)
    → Có token, validate thành công → set SecurityContext → tiếp tục → vào Controller*/

/*Filter này chạy 1 lần duy nhất mỗi request do OncePerRequestFilter.
        - Đọc JWT từ Header, validation, set authentication vào SecurityContext để Spring Security biết request này đã được xác thực*/

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final FactUserDetailsService factUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if(request.getRequestURI()
                .startsWith("/api/payments/webhook")) {
            filterChain.doFilter(request,response);
            return;
        }

        //Đọc Authorization từ Header
        final String authHeader = request.getHeader("Authorization");
        //Nếu không có header hoặc header không bắt đầu bằng "Bearer " thì tiếp xuống filter tiếp theo (không bắt exception vì có thể request public không cần token)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7); // Bỏ "Bearer "
        final String email;
        try {
            email = jwtUtil.extractUsername(token);
        } catch (JwtException e) {
            filterChain.doFilter(request, response);
            return;
        }

        //Nếu token parse được, không lỗi và chưa có authentication trong context (tránh xử lí lại)
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //Load user từ DB theo email, check validate token xem token có đúng với user này không và còn hạn token không
            UserDetails userDetails = factUserDetailsService.loadUserByUsername(email);
            if (jwtUtil.validateToken(token, userDetails)) {
                //Tạo authentication object với userDetails và authorities (roles)
                //credentials null: không cần thiết vì đã validate token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                //thêm thông tin IP, session vào authentication
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //set vào SecurityContextHolder --> Spring Security coi request này đã được authenticated.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}

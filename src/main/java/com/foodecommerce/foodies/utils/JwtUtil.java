package com.foodecommerce.foodies.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


///*POST /login
//username/password -- > AuthenticationManager --> UserDetailsService ==> generateToken() --> JWT trả về client
@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private Long expiration;

    //Chuyển chuỗi SECRET_KEY của bạn thành mã SecretKey bảo mật
    // Key bắt buộc phải có độ dài từ 32 ký tự (256-bit) trở lên
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)); //HS256 - 32 byte (1 byte = 8 bit)
    }

    //được gọi khi login success
    //User nhập success --> Spring security trả về UserDetails (userDetails.getUsername()) ---> tạo token
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        //JWT: HEADER.PAYLOAD.SIGNATURE
        //claims: thêm payload, subject = username
        return Jwts.builder().claims(claims).subject(subject).issuedAt(new Date()) // 1000 ms = 1s
                .expiration(new Date(System.currentTimeMillis() + expiration)) //12h
                .signWith(getSecretKey(), Jwts.SIG.HS256) //add thuật toán
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //tái sử dụng
    //Flow: JWT --> extractAllClaims ---> Claims --> Function --> data
    //VD: Claims :: getSubject
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

   //Verify
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey()) //Secret check signature
                .build()
                .parseSignedClaims(token)//Decode header payload signature
                .getPayload(); //trả claims
    }


    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}

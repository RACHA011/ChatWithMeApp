// package com.racha.ChatWithMe.utils.token;

// import io.jsonwebtoken.*;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.stereotype.Component;

// import java.security.Key;
// import java.util.Date;

// @Component
// public class JwtUtils {

//     // openssl rand -base64 32 (run it in sudo)
//     private static final String SECRET_KEY = "1qqw37KB35NH5XvYGt5G7mMes5ZBIN2oAJOUL7DmV4g="; 
//     private static final long EXPIRATION_TIME = 60 * 60 * 1000; // 1 hour

//     private Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

//     public String generateToken(String email) {
//         return Jwts.builder()
//                 .setSubject(email)
//                 .setIssuedAt(new Date())
//                 .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                 .signWith(key, SignatureAlgorithm.HS256)
//                 .compact();
//     }

//     public boolean validateToken(String token) {
//         try {
//             Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//             return true;
//         } catch (JwtException | IllegalArgumentException e) {
//             return false; // Token is invalid
//         }
//     }

//     public String getEmailFromToken(String token) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(key)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody()
//                 .getSubject();
//     }
// }

package com.example.exampleboard.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.exampleboard.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtProvider {
	
	@Value("{secret.jwt.key}")
	private String secretKey;
	
	// 토큰 생성
	public String generateToken(User user) {
				// 헤더
				Map<String, Object> headers = new HashMap<>();
		        headers.put("typ", "JWT");
		        headers.put("alg", "HS256");
		        
		        // 정보(payload)
		        Map<String, Object> payloads = new HashMap<>();
		        payloads.put("Id", user.getId());
		        payloads.put("name", user.getName());
		        
		        Date currentDate = new Date();
		        Date expirationDate = new Date(currentDate.getTime() + 60*1000*60);
		        
		        return Jwts.builder()
		                .setHeader(headers)
		                .setClaims(payloads)
		                .setIssuedAt(currentDate)
		                .setExpiration(expirationDate)
		                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes()) // Signature
		                .compact();
	}
	
	// 토큰 파싱
	public Long getToken(String option, String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
                
        return  claims.get(option, Long.class);
}
}

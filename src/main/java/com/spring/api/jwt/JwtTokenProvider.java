package com.spring.api.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class JwtTokenProvider {
	private static String PRIVATE_KEY ="ABCDEF";
	private static long ACCESSTOKEN_EXPIRATION_TIME = 2 * 60 * 60 * 1000L;
	private static long REFRESHTOKEN_EXPIRATION_TIME = 14 * 24 * 60 * 60 * 1000L;
	
	@Value("${jwt.privatekey}")
	public void setPRIVATE_KEY(String PRIVATE_KEY) {
		this.PRIVATE_KEY = PRIVATE_KEY;
	}
	
	public static long getAccesstokenExpirationTime() {
		return ACCESSTOKEN_EXPIRATION_TIME;
	}
	
	public static long getRefreshtokenExpirationTime() {
		return REFRESHTOKEN_EXPIRATION_TIME;
	}
	
	public static String createToken(Authentication authentication, boolean isAccessToken) {
        Date now = new Date();
        
        if(isAccessToken) {
        	Date expiryDate = new Date(now.getTime() + ACCESSTOKEN_EXPIRATION_TIME);
        	return Jwts.builder()
                    .setSubject((String) authentication.getPrincipal()) // 사용자
                    .setIssuedAt(new Date()) // 현재 시간 기반으로 생성
                    .setExpiration(expiryDate) // 만료 시간 세팅
                    .claim("token_type", "user_accesstoken")
                    .signWith(SignatureAlgorithm.HS512, PRIVATE_KEY) // 사용할 암호화 알고리즘, signature에 들어갈 secret 값 세팅
                    .compact();
        }else {
        	Date expiryDate = new Date(now.getTime() + REFRESHTOKEN_EXPIRATION_TIME);
        	return Jwts.builder()
                    .setSubject((String) authentication.getPrincipal()) // 사용자
                    .setIssuedAt(new Date()) // 현재 시간 기반으로 생성
                    .setExpiration(expiryDate) // 만료 시간 세팅
                    .claim("token_type", "user_refreshtoken")
                    .signWith(SignatureAlgorithm.HS512, PRIVATE_KEY) // 사용할 암호화 알고리즘, signature에 들어갈 secret 값 세팅
                    .compact();
        }        
    }
	
    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(PRIVATE_KEY).parseClaimsJws(token);
            return true;
        }catch (SignatureException e) {
            System.out.println("Invalid JWT signature");
        }catch (MalformedJwtException e) {
        	System.out.println("Invalid JWT token");
        }catch (ExpiredJwtException e) {
        	System.out.println("Expired JWT token");
        }catch (UnsupportedJwtException e) {
        	System.out.println("Unsupported JWT token");
        }catch (IllegalArgumentException e) {
        	System.out.println("JWT claims string is empty");
        }catch(Exception e) {
        	System.out.println("Unknown Error");
        }
        return false;
    }
    
    public static String getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(PRIVATE_KEY)
            .parseClaimsJws(token)
            .getBody();

        return claims.getSubject();
    }
    
    public static String getTokenType(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(PRIVATE_KEY)
            .parseClaimsJws(token)
            .getBody();

        return (String) claims.get("token_type");
    }
}
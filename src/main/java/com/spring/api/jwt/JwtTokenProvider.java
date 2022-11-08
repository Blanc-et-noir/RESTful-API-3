package com.spring.api.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {
	private static String PRIVATE_KEY = null;
	private final static long ACCESSTOKEN_EXPIRATION_TIME = 2 * 60 * 60 * 1000L;
	private final static long REFRESHTOKEN_EXPIRATION_TIME = 14 * 24 * 60 * 60 * 1000L;
	
	@Value("${jwt.privatekey}")
	public void setPRIVATE_KEY(String PRIVATE_KEY) {
		this.PRIVATE_KEY = PRIVATE_KEY;
	}
	
	public long getAccesstokenExpirationTime() {
		return ACCESSTOKEN_EXPIRATION_TIME;
	}
	
	public long getRefreshtokenExpirationTime() {
		return REFRESHTOKEN_EXPIRATION_TIME;
	}
	
	public String createToken(Authentication authentication, boolean isAccessToken) {
        Date now = new Date();
        Date expiryDate = null;
        String token_type = null;
        
        if(isAccessToken) {
        	expiryDate = new Date(now.getTime() + ACCESSTOKEN_EXPIRATION_TIME);
        	token_type = "user_accesstoken";
        }else {
        	expiryDate = new Date(now.getTime() + REFRESHTOKEN_EXPIRATION_TIME);
        	token_type = "user_refreshtoken";
        }
        
        return Jwts.builder()
                .setSubject((String) authentication.getPrincipal())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .claim("token_type", token_type)
                .signWith(SignatureAlgorithm.HS512, PRIVATE_KEY)
                .compact();
    }
	
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(PRIVATE_KEY).parseClaimsJws(token);
            return true;
        }catch(Exception e) {
        	return false;
        }
    }
    
    public String getUserIdFromJWT(String token) {
    	try {
    		Claims claims = Jwts.parser()
    	            .setSigningKey(PRIVATE_KEY)
    	            .parseClaimsJws(token)
    	            .getBody();

    	        return claims.getSubject();
    	}catch(ExpiredJwtException  e) {
    		return (String) e.getClaims().getSubject();
    	}catch(Exception e) {
    		return "INVALID";
    	}
    }
    
    public String getTokenType(String token) {
    	try {
    		Claims claims = Jwts.parser()
    	            .setSigningKey(PRIVATE_KEY)
    	            .parseClaimsJws(token)
    	            .getBody();
    		return (String) claims.get("token_type");
    	}catch(ExpiredJwtException  e) {
    		return (String) e.getClaims().get("token_type");
    	}catch(Exception e) {
    		return "INVALID";
    	}
    }
    
    public long getRemainingTime(String token) {
    	try {
    		long time = Jwts.parser().setSigningKey(PRIVATE_KEY).parseClaimsJws(token).getBody().getExpiration().getTime() - new Date().getTime();
    		if(time>0) {
    			return time;
    		}else {
    			return 0;
    		}
    	}catch(Exception e) {
    		return 0;
    	}
    }
}
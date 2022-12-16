package com.spring.api.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;

@Component
@Getter
public class JwtTokenProvider {
	private final String PRIVATE_KEY;
	private final long ACCESSTOKEN_EXPIRATION_TIME;
	private final long REFRESHTOKEN_EXPIRATION_TIME;

	@Autowired
	JwtTokenProvider(
		@Value("${jwt.privatekey}") String PRIVATE_KEY,
		@Value("${jwt.accesstoken.expiration.time}") long ACCESSTOKEN_EXPIRATION_TIME,
		@Value("${jwt.refreshtoken.expiration.time}") long REFRESHTOKEN_EXPIRATION_TIME
	){
		this.PRIVATE_KEY = PRIVATE_KEY;
		this.ACCESSTOKEN_EXPIRATION_TIME = ACCESSTOKEN_EXPIRATION_TIME;
		this.REFRESHTOKEN_EXPIRATION_TIME = REFRESHTOKEN_EXPIRATION_TIME;
	}
	
	public String createToken(Authentication authentication, boolean isAccessToken) {
        Date now = new Date();
        Date expiryDate = null;
        
        String user_role = authentication.getAuthorities().iterator().next().getAuthority();
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
                .claim("user_role", user_role)
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
    
    public String getUserRoleFromJWT(String token) {
    	try {
    		Claims claims = Jwts.parser()
    	            .setSigningKey(PRIVATE_KEY)
    	            .parseClaimsJws(token)
    	            .getBody();

    	        return (String) claims.get("user_role");
    	}catch(ExpiredJwtException  e) {
    		return (String) e.getClaims().get("user_role");
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
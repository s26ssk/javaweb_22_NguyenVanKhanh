package com.la.javaweb.security.jwt;

import com.la.javaweb.security.user_principal.UserPrincipal;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {
	
	private final Logger logger = LoggerFactory.getLogger(JwtEntryPoint.class);
	
	@Value("${jwt.secret-key}")
	private String SECRET_KEY;
	
	@Value("${jwt.expired}")
	private Long EXPIRED;
	
	public String generateToken(UserPrincipal userPrincipal) {
		return Jwts.builder().setSubject(userPrincipal.getUsername())
				  .setIssuedAt(new Date())
				  .setExpiration(new Date(new Date().getTime() + EXPIRED))
				  .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
				  .compact();
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token); //Lưu ý chính tả
			return true;
			
		} catch (ExpiredJwtException e) {
			logger.error("Failed -> Expired Token Message {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("Failed -> Unsupported Token Message {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Failed -> Invalid Format Token Message {}", e.getMessage());
		} catch (SignatureException e) {
			logger.error("Failed -> Invalid Signature Token Message {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("Failed -> Claims Empty Token Message {}", e.getMessage());
		}
		return false;
	}
	
	public String getUsernameFromToken(String token) {
		return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
	}
	
	
}

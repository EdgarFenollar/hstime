package com.project.hstime.security.jwt;


import com.project.hstime.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${bezkoder.app.jwtSecret}")
  private String jwtSecret;

  @Value("${bezkoder.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  // Generación del JWT token
  public String generateJwtToken(Authentication authentication) {
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    // Crear y firmar el JWT
    return Jwts.builder()
            .setSubject(userPrincipal.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512) // Algoritmo de firma
            .compact();
  }

  // Obtener la clave secreta para firmar el JWT
  private SecretKey getSigningKey() {
    // Use the secretKeyFor method to generate a 512-bit key for HS512
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }



  // Extraer el nombre de usuario desde el token JWT
  public String getEmailFromJwtToken(String token) throws JwtException {
    if (!StringUtils.hasText(token)) {
      logger.warn("Attempted to parse empty or null JWT token");
      return null;
    }

    try {
      Claims claims = Jwts.parser()
              .verifyWith(getSigningKey())
              .build()
              .parseSignedClaims(token)
              .getPayload();

      String email = claims.getSubject();

      if (!StringUtils.hasText(email)) {
        logger.error("JWT token has empty subject (email)");
        return null;
      }

      // Optional: Validate email format if needed
      if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
        logger.warn("JWT token contains invalid email format: {}", email);
        return null;
      }

      return email;
    } catch (ExpiredJwtException ex) {
      logger.warn("Expired JWT token: {}", ex.getMessage());
      throw ex;
    } catch (MalformedJwtException ex) {
      logger.error("Invalid JWT token: {}", ex.getMessage());
      throw ex;
    } catch (JwtException ex) {
      logger.error("JWT processing error: {}", ex.getMessage());
      throw ex;
    }
  }

  // Validar si el token JWT es válido
  public boolean validateJwtToken(String authToken) {
    try {
      // Verificar la firma del JWT
      Jwts.parser()
              .setSigningKey(getSigningKey())
              .build()
              .parse(authToken);
      return true;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    } catch (SignatureException e) {
      logger.error("JWT signature does not match locally computed signature: {}", e.getMessage());
    }

    return false;
  }
}

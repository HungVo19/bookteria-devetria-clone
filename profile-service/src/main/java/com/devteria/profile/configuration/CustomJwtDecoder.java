package com.devteria.profile.configuration;

import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
public class CustomJwtDecoder implements JwtDecoder {

  @Override
  public Jwt decode(String token) throws JwtException {
    // gateway already check token then no need to duplicate introspection here
    // but token is still required for other information so we cant remove all security configs
    // for example we still need PreAuthorized annotation, ...
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return new Jwt(token, signedJWT.getJWTClaimsSet().getIssueTime().toInstant(),
          signedJWT.getJWTClaimsSet().getExpirationTime().toInstant(),
          signedJWT.getHeader().toJSONObject(),
          signedJWT.getJWTClaimsSet().toJSONObject());
    } catch (ParseException e) {
      throw new JwtException("Invalid token");
    }
  }
}

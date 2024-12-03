package com.devteria.identity.configuration;

import com.devteria.identity.service.AuthenticationService;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class CustomJwtDecoder implements JwtDecoder {

  private final AuthenticationService authenticationService;
  @Value("${jwt.signerKey}")
  private String signerKey;
  private NimbusJwtDecoder nimbusJwtDecoder = null;

  public CustomJwtDecoder(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @Override
  public Jwt decode(String token) throws JwtException {
//
//        var response = authenticationService.introspect(
//                IntrospectRequest.builder().token(token).build());
//
//        if (!response.isValid()) throw new JwtException("Token invalid");
//
//        if (Objects.isNull(nimbusJwtDecoder)) {
//            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
//            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
//                    .macAlgorithm(MacAlgorithm.HS512)
//                    .build();
//        }
//
//        return nimbusJwtDecoder.decode(token);

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

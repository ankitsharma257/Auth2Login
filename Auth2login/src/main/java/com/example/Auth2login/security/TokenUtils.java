package com.example.Auth2login.security;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.Auth2login.user.UserPrincipal;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TokenUtils {

	@Autowired
	TokenProperties tokenProperties;

	@Autowired
	private RSASSASigner signer;

	@Autowired
	private RSASSAVerifier verifier;

	public String createToken(Authentication authentication) throws JOSEException 
	{
		System.out.println("inside token creation");
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		System.out.println(userPrincipal);
		log.debug("Principal {}", authentication);
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + tokenProperties.getExpireMillis());
		JWTClaimsSet claims = new JWTClaimsSet.Builder()
				.subject(userPrincipal.getSubject())
				.issueTime(now)
				.expirationTime(expiryDate)
				.claim("roles", userPrincipal.getAuthorities()
						.stream()
						.map(GrantedAuthority::getAuthority)
						.collect(Collectors.joining(",")))
				.build();
		SignedJWT token = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
		token.sign(signer);
		return token.serialize();
	}

	public UserPrincipal getPrincipalFromToken(String authToken) throws ParseException, JOSEException {

		SignedJWT token = SignedJWT.parse(authToken);
		if (!token.verify(verifier))
			throw new JOSEException("Signature not verified");
		JWTClaimsSet claims = token.getJWTClaimsSet();
		if (claims.getExpirationTime().before(new Date()))
			throw new JOSEException("Expired");
		return new UserPrincipal(claims.getSubject(),
				Arrays.stream(claims.getClaim("roles").toString().split(","))
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList()));
	}

}

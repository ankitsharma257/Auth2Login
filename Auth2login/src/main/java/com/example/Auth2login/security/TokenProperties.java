package com.example.Auth2login.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "app.security.token")
@Data
public class TokenProperties {

	private int expireMillis;
	private String alias;
	private String path;
	private String type;
	private String password;

}

package com.example.Auth2login.user;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class User {

	@Id
	private String id;

	@Indexed(unique = true)
	private String email;

	private String FirstName;

	private String LastName;

	private String photoUrl;

	private String phoneNumber;

	private List<RoleType> roles;
}

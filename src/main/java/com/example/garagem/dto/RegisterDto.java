package com.example.garagem.dto;

import lombok.Data;

import java.util.List;

@Data
public class RegisterDto {
	private String username;
	private String name;
	private String email;
	private String password;
	private List<String> roles;
}

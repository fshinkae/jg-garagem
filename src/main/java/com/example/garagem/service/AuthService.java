package com.example.garagem.service;

import com.example.garagem.dto.LoginDto;
import com.example.garagem.dto.RegisterDto;
import com.example.garagem.model.User;

import java.util.Optional;

public interface AuthService {
	String login(LoginDto loginDto);
	String registerUser(RegisterDto registerDto);
	Optional<User> searchUser(String email);
}

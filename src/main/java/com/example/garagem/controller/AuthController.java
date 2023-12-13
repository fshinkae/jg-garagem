package com.example.garagem.controller;

import com.example.garagem.dto.JWTAuthResponse;
import com.example.garagem.dto.LoginDto;
import com.example.garagem.dto.RegisterDto;
import com.example.garagem.service.AuthService;

import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<?> authenticate(@RequestBody LoginDto loginDto) {
		try {
			String token = authService.login(loginDto);

			JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
			jwtAuthResponse.setAccessToken(token);

			return ResponseEntity.ok(jwtAuthResponse);
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro durante a autenticação.");
		}
	}


	@PostMapping("/register")
	public ResponseEntity<String> registerUser(
			@RequestBody RegisterDto registerDto) {
		String message;
		HttpStatus status;

		if (registerDto.getRoles() == null || registerDto.getRoles().isEmpty()) {
			message = "Você deve fornecer pelo menos uma função (role) para o usuário.";
			status = HttpStatus.BAD_REQUEST;
		} else {
			message = authService.registerUser(registerDto);
			status = HttpStatus.CREATED;
		}

		return new ResponseEntity<>(message, status);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteByEmail(@RequestBody String email) {
		String message;
		HttpStatus status;
		System.out.println(email);
		try {
			authService.deleteByEmail(email);
			message = "Usuário deletado com sucesso.";
			status = HttpStatus.OK;
		} catch (Exception e) {
			message = "Ocorreu um erro durante a deleção do usuário.";
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		return new ResponseEntity<>(message, status);
	}


}

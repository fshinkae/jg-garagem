package com.example.garagem.service;

import com.example.garagem.dto.LoginDto;
import com.example.garagem.dto.RegisterDto;
import com.example.garagem.model.User;
import com.example.garagem.model.Role;
import com.example.garagem.repository.RoleRepository;
import com.example.garagem.repository.UserRepository;
import com.example.garagem.util.JwtTokenProvider;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

	private AuthenticationManager authenticationManager;
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private JwtTokenProvider jwtTokenProvider;
	private RoleRepository roleRepository;


	public AuthServiceImpl(
			JwtTokenProvider jwtTokenProvider,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			RoleRepository roleRepository) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
		this.roleRepository = roleRepository;
	}

	@Override
	public String login(LoginDto loginDto) {
		try {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					loginDto.getEmail(), loginDto.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return jwtTokenProvider.generateToken(authentication);
		} catch (AuthenticationException e) {
			throw new BadCredentialsException("Falha na autenticação: Credenciais inválidas", e);
		}
	}

	@Override
	public String registerUser(RegisterDto registerDto) {
		if (userRepository.existsByUsername(registerDto.getUsername())) {
			throw new RuntimeException("Nome de usuário já está em uso.");
		}

		if (userRepository.existsByEmail(registerDto.getEmail())) {
			throw new RuntimeException("O endereço de e-mail já está em uso.");
		}

		User user = new User();
		user.setUsername(registerDto.getUsername());
		user.setName(registerDto.getName());
		user.setEmail(registerDto.getEmail());
		user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

		Set<Role> roles = new HashSet<>();

		String roleToAssign = registerDto.getRoles().get(0);

		Optional<Role> role = roleRepository.findByName(roleToAssign);
		if (role.isEmpty()) {
			throw new RuntimeException("A role '" + roleToAssign + "' não foi encontrada.");
		}
		roles.add(role.get());

		user.setRoles(roles);

		userRepository.save(user);

		return "Usuário registrado com sucesso.";
	}

	@Override
	public Optional<User> searchUser(String email) {
		if (email == null || email.isEmpty()) {
			throw new IllegalArgumentException("Email não pode ser nulo ou vazio.");
		}
		return userRepository.findByEmail(email);
	}

	@Override
	public void deleteByEmail(String email) {
		Optional<User> user = searchUser(email);
		if (email == null || email.isEmpty()) {
			throw new IllegalArgumentException("Email não pode ser nulo ou vazio.");
		} else if (user.isEmpty()) {
			throw new RuntimeException("O usuário com o email '" + email + "' não foi encontrado.");
		}
		userRepository.deleteByEmail(email);
	}

}


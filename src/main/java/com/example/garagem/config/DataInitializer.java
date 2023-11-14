package com.example.garagem.config;

import com.example.garagem.model.Role;
import com.example.garagem.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.Set;

@Configuration
public class DataInitializer {

	@Bean
	public CommandLineRunner initialize(RoleRepository roleRepository) {
		return args -> {
			initializeRoleIfNotExists(roleRepository, "ROLE_ADMIN");
			initializeRoleIfNotExists(roleRepository, "ROLE_CUSTOMER");
			initializeRoleIfNotExists(roleRepository, "ROLE_SELLER");
		};
	}

	private void initializeRoleIfNotExists(RoleRepository roleRepository, String roleName) {
		Optional<Role> roleOptional = roleRepository.findByName(roleName);
		if (roleOptional.isEmpty()) {
			Role role = new Role();
			role.setName(roleName);
			roleRepository.save(role);
		}
	}
}

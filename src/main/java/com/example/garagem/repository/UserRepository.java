package com.example.garagem.repository;

import com.example.garagem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	Boolean existsByEmail(String email);

	Optional<User> findByUsernameOrEmail(String username, String email);

	boolean existsByUsername(String username);

	@Modifying
	@Transactional
	void deleteByEmail(String email);
}

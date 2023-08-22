package com.example.exampleboard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.exampleboard.model.User;

public interface JpaUserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);
	Optional<User> findByName(String name);
	Optional<User> findByPassword(String password);
}

package com.example.exampleboard.repository;

import java.util.Optional;

import com.example.exampleboard.model.User;

public interface UserRepository {
	User save(User user);
	Optional<User> findById(Long id);
	Optional<User> findByEmail(String email);
	boolean check(User user);
}

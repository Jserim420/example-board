package com.example.exampleboard.service;



import org.springframework.transaction.annotation.Transactional;

import com.example.exampleboard.model.User;
import com.example.exampleboard.repository.UserRepository;
import com.fasterxml.jackson.databind.node.BooleanNode;

@Transactional
public class UserService {
	
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository=userRepository;
	}
	
	public Long join(User user) {
		if(isUser(user)) return null;
		else {
			userRepository.save(user);
			return user.getId();
		}
	}
	
	public Boolean isUser(User user) {
		
		if(!userRepository.findByEmail(user.getEmail()).isPresent()) return false;
		else return true;
	}
	
	public Long login(User user) {
		checkUser(user);
		return user.getId();
	}
	
	public User checkUser(User user) {
		System.out.println(user.getEmail());
		if(!isUser(user)) return null ; // 없는 사용자면 null
		else if(!userRepository.check(user)) return null; // 비밀번호 틀릴때
		else return user;
		
	}
}
	

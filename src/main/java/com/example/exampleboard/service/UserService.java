package com.example.exampleboard.service;



import org.springframework.transaction.annotation.Transactional;

import com.example.exampleboard.model.User;
import com.example.exampleboard.repository.JdbcUserRepository;
import com.fasterxml.jackson.databind.node.BooleanNode;

@Transactional
public class UserService {
	
	private final JdbcUserRepository jdbcUserRepository;

	public UserService(JdbcUserRepository jdbcUserRepository) {
		this.jdbcUserRepository=jdbcUserRepository;
	}
	
	public Long join(User user) {
		if(!isUser(user) && !isName(user)) { // 가입된 사용자가 없다면
			jdbcUserRepository.save(user);
			return null;
		}
			return user.getId();
	}
	
	public Boolean isUser(User user) {
		// 가입된 메일이 없다면
		if(!jdbcUserRepository.findByEmail(user.getEmail()).isPresent()) return false;
		else return true;
	}
	
	public Boolean isName(User user) {
		// 가입된 닉네임이 없다면
		if(!jdbcUserRepository.findByName(user.getName()).isPresent()) return false;
		else return true;
	}
	
	public Long login(User user) {
		checkUser(user);
		return user.getId();
	}
	
	public User checkUser(User user) {
		System.out.println(user.getEmail());
		if(!isUser(user)) return null ; // 없는 사용자면 null
		else if(!jdbcUserRepository.check(user)) return null; // 비밀번호 틀릴때
		else return user; // 로그인 성공
		
	}
}
	

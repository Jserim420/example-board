package com.example.exampleboard.service;



import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.example.exampleboard.model.User;
import com.example.exampleboard.repository.JpaUserRepository;

@Transactional
public class UserService {
	
	private final JpaUserRepository userRepository;
	private final BCryptPasswordEncoder encoder;

	@Autowired
	public UserService(JpaUserRepository userRepository, BCryptPasswordEncoder encoder) {
		this.userRepository = userRepository;
		this.encoder = encoder;
	}
	
	// 회원가입
	public Long join(User user) {
		if(isName(user)==null && isEmail(user)==null) {
			user.setPassword(encoder.encode(user.getPassword()));
			userRepository.save(user);
			System.out.println("저장");
			return null;
		} else return user.getId();
	}
	
	//회원찾기
	public User findUser(Long id) {
		return userRepository.findById(id).get();
	}
	
	// 이메일중복체크
	public User isEmail(User user) {
		if(userRepository.findByEmail(user.getEmail()).isPresent()) 
			return userRepository.findByEmail(user.getEmail()).get();
		else return null;
	}
	
	
	// 닉네임중복체크
	public User isName(User user) {
		if(userRepository.findByName(user.getName()).isPresent()) return user;
		else return null;
	}
	
	// 아이디&비밀번호 체크
		public boolean checkUser(User user) {
			if(userRepository.findByEmail(user.getEmail()).isEmpty()) return false;
			else return encoder.matches(user.getPassword(), 
					userRepository.findByEmail(user.getEmail()).get().getPassword());
		}
		
	// 닉네임 포함 유무 체크
		public Optional<User> searchName(String name) {
			return userRepository.findByNameContaining(name).stream().findAny();
		}
}
	

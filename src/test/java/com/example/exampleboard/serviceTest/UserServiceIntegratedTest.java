package com.example.exampleboard.serviceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.exampleboard.model.User;
import com.example.exampleboard.repository.JdbcUserRepository;
import com.example.exampleboard.service.UserService;

@SpringBootTest
@Transactional
public class UserServiceIntegratedTest {
	
	@Autowired UserService userService;
	@Autowired JdbcUserRepository JdbcUserRepository;


	@Test
	public void 회원가입() throws Exception {
		//Given
		User user = new User();
		user.setEmail("test@naver.com");
		user.setPassword("1234");
		user.setName("테스트");
		
		
		//When
		Long saveId = userService.join(user);
		
		//Then
		User findUser = JdbcUserRepository.findById(saveId).get();
		assertEquals(user.getEmail(), findUser.getEmail());
	}

	@Test
	public void 중복회원() throws Exception {
		//Given
		User user =new User();
		user.setEmail("spring@naver.com");
		user.setPassword("1234");
		user.setName("스프링");
		
		User user2 =new User();
		user2.setEmail("spring@naver.com");
		user2.setPassword("1234");
		user2.setName("스프링");
		
		//When
		userService.join(user);
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> userService.join(user2));
		
		//Then
		assertThat(exception.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");
	}
	
	@Test
	public void 로그인() throws Exception {
		//Given
		User user = new User();
		user.setEmail("java@naver.com");
		user.setPassword("1234");
		user.setName("자바");
		
		User user2 = new User();
		user2.setEmail("java2@naver.com");
		user2.setPassword("1234");
		
		User user3 = new User();
		user2.setEmail("java@naver.com");
		user2.setPassword("5678");
		
		//When
		userService.join(user);
//		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> userService.checkUser(user2));
//		IllegalStateException exception2 = assertThrows(IllegalStateException.class, () -> userService.checkUser(user3));
		
		
		//Then
//		assertThat(exception2.getMessage()).isEqualTo("존재하지 않는 아이디입니다.");
//		assertThat(exception.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
	}
}

package com.example.exampleboard.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.exampleboard.MelonSelenium;
import com.example.exampleboard.repository.JpaBoardRepository;
import com.example.exampleboard.repository.JpaCommentRepository;
import com.example.exampleboard.repository.JpaUserRepository;
import com.example.exampleboard.service.BoardService;
import com.example.exampleboard.service.CommentService;
import com.example.exampleboard.service.JwtProvider;
import com.example.exampleboard.service.UserService;


@Configuration
public class SpringConfig {

	
	private final JpaBoardRepository boardRepository;
	private final JpaUserRepository userRepository;
	private final JpaCommentRepository commentRepository;
	
	public SpringConfig(JpaBoardRepository boardRepository, JpaUserRepository userRepository, JpaCommentRepository commentRepository) {
		this.boardRepository = boardRepository;
		this.userRepository = userRepository;
		this.commentRepository = commentRepository;
	}
	
	
	@Bean
	public UserService userService() {
		return new UserService(userRepository, new BCryptPasswordEncoder());
	}
	
	
	@Bean
	public BoardService boardService() {
		return new BoardService(boardRepository);
	}
	
	@Bean
	public CommentService commentService() {
		return new CommentService(commentRepository);
	}
	
	@Bean
	public JwtProvider jwtProvider() {
		return new JwtProvider();
	}
	
	@Bean
	public MelonSelenium melonSelenium() {
		return new MelonSelenium();
	}
	
	
}

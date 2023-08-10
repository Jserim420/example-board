package com.example.exampleboard.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.exampleboard.repository.JdbcBoardRepository;
import com.example.exampleboard.repository.JdbcCommentRepository;
import com.example.exampleboard.repository.JdbcUserRepository;
import com.example.exampleboard.repository.UserRepository;
import com.example.exampleboard.service.BoardService;
import com.example.exampleboard.service.CommentService;
import com.example.exampleboard.service.UserService;

@Configuration
public class SpringConfig {

	private final DataSource dataSource;
	
	public SpringConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	
	@Bean
	public UserService userService() {
		return new UserService(userRepository());
	}
	
	@Bean
	public BoardService boardService() {
		return new BoardService(jdbcBoardRepository());
	}
	
	@Bean
	public JdbcBoardRepository jdbcBoardRepository() {
		return new JdbcBoardRepository(dataSource, userRepository());
	}
	
	@Bean
	public UserRepository userRepository() {
		//return new MemoryUserRepository();
		return new JdbcUserRepository(dataSource);
	}
	
	@Bean
	public CommentService commentService() {
		return new CommentService(commentRepository());
	}
	
	@Bean
	public JdbcCommentRepository commentRepository() {
		return new JdbcCommentRepository(dataSource);
	}
	
}

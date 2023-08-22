package com.example.exampleboard.repository;


import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.exampleboard.model.User;

public class JdbcUserRepository{

	private final JdbcTemplate jdbcTemplate;
	
	
	
	public JdbcUserRepository(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	// 사용자 저장 insert
	public User save(User user) {
//		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
//		jdbcInsert.withTableName("TB_user").usingGeneratedKeyColumns("id");
//		Map<String,  Object> parameters = new HashMap<>();
//		parameters.put("email", user.getEmail());
//		String encodePassword = encoder.encode(user.getPassword());
//		parameters.put("password", encodePassword);
//		parameters.put("name", user.getName());
//		
//		Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters)); //PK값 얻기
//		user.setId(key.longValue());
//		jdbcTemplate.update("insert into TB_user(email, password, name) values(?, ?, ?)",
//				user.getEmail(), encodePassword, user.getName());
		
		return user;
	}

	
	// 사용자 id를 바탕으로 사용자 검색
	public Optional<User> findById(Long id) {
		List<User> result = jdbcTemplate.query("select * from TB_user where id = ?", 
																		userRowMapper(), id);
		return result.stream().findAny();
	}
	
	// email을 바탕으로 사용자 검색
	public Optional<User> findByEmail(String email) {
		List<User> result = jdbcTemplate.query("select * from TB_user where email = ?", userRowMapper(), email);
		return result.stream().findAny();
	}
	
	// name을 바탕으로 사용자 검색
	public Optional<User> findByName(String name) {
		List<User> result = jdbcTemplate.query("select * from TB_user where name = ?", userRowMapper(), name);
		return result.stream().findAny();
	}
	
	
	// 추출한 데이터를 객체에 저장
	private RowMapper<User> userRowMapper() {
		return (rs, rowNum) -> {
			User user = new User();
			user.setId(rs.getLong("id"));
			user.setEmail(rs.getString("email"));
			user.setPassword(rs.getString("password"));
			user.setName(rs.getString("name"));
			
			return user;
		};
	}

	// 사용자가 올바른 로그인을 시도한것인지 확인(비밀번호 DB일치 확인)
	public boolean check(User user) {
		User findUser = findByEmail(user.getEmail()).get();
		String userPassword = findUser.getPassword();
		System.out.println(userPassword);
		System.out.println(user.getPassword());
		return false;
	}


	
}

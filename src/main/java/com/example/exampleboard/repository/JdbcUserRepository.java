package com.example.exampleboard.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.exampleboard.model.User;

public class JdbcUserRepository implements UserRepository{

	private final JdbcTemplate jdbcTemplate;
	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	
	public JdbcUserRepository(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public User save(User user) {
//		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
//		jdbcInsert.withTableName("TB_user").usingGeneratedKeyColumns("id");
//		Map<String,  Object> parameters = new HashMap<>();
//		parameters.put("email", user.getEmail());
		String encodePassword = encoder.encode(user.getPassword());
//		parameters.put("password", encodePassword);
//		parameters.put("name", user.getName());
//		
//		Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters)); //PK값 얻기
//		user.setId(key.longValue());
		jdbcTemplate.update("insert into TB_user(email, password, name) values(?, ?, ?)",
				user.getEmail(), encodePassword, user.getName());
		
		return user;
	}

	
	@Override
	public Optional<User> findById(Long id) {
		List<User> result = jdbcTemplate.query("select * from TB_user where id = ?", 
																		userRowMapper(), id);
		return result.stream().findAny();
	}
	
	@Override
	public Optional<User> findByEmail(String email) {
		List<User> result = jdbcTemplate.query("select * from TB_user where email = ?", userRowMapper(), email);
		return result.stream().findAny();
	}
	
	@Override
	public Optional<User> findByName(String name) {
		List<User> result = jdbcTemplate.query("select * from TB_user where name = ?", userRowMapper(), name);
		return result.stream().findAny();
	}
	
	
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

	@Override
	public boolean check(User user) {
		User findUser = findByEmail(user.getEmail()).get();
		String userPassword = findUser.getPassword();
		System.out.println(userPassword);
		System.out.println(user.getPassword());
		return encoder.matches(user.getPassword(), userPassword);
	}


	
}

package com.example.exampleboard.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.example.exampleboard.model.Board;
import com.example.exampleboard.model.Comment;


public class JdbcCommentRepository {
	private final JdbcTemplate jdbcTemplate;
	
	public JdbcCommentRepository(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public Comment save(Comment comment) {
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
		jdbcInsert.withTableName("TB_comment").usingGeneratedKeyColumns("id");
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", comment.getId());
		parameters.put("body", comment.getBody());
		parameters.put("boardID", comment.getBoardId());
		parameters.put("userName", comment.getUserName());
		parameters.put("password", comment.getPassword());
		parameters.put("writeDate", comment.getWriteDate());
		parameters.put("likeCount", comment.getLikeCount());		
	
		Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
		comment.setId(key.longValue());
		return comment;
	}
	
	public List<Comment> findByAll(Long id) {
		return jdbcTemplate.query("select * from TB_comment where boardId= ?", commentRowMapper(), id);
	}
	
//	public List<Comment> findByAll(Board board) {
//		
//	}
	
	private RowMapper<Comment> commentRowMapper() {
		return (rs, rowNum) -> {
			Comment com = new Comment();
			com.setId(rs.getLong("id"));
			com.setBody(rs.getString("body"));
			com.setWriteDate(rs.getDate("writeDate"));
			com.setUserName(rs.getString("userName"));
			com.setPassword(rs.getString("password"));
			com.setLikeCount(rs.getInt("LikeCount"));
			
			return com;
		};
	}
	
}

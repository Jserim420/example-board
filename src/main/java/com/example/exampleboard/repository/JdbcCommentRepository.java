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

import com.example.exampleboard.model.Board;
import com.example.exampleboard.model.Comment;


public class JdbcCommentRepository {
	private final JdbcTemplate jdbcTemplate;
	
	public JdbcCommentRepository(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public Comment save(Comment comment) {
//		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
//		jdbcInsert.withTableName("TB_comment").usingGeneratedKeyColumns("id");
//		Map<String, Object> parameters = new HashMap<>();
//		parameters.put("id", comment.getId());
//		parameters.put("body", comment.getBody());
//		parameters.put("boardID", comment.getBoardId());
//		parameters.put("userName", comment.getUserName());
//		parameters.put("password", comment.getPassword());
//		parameters.put("writeDate", comment.getWriteDate());
//		parameters.put("likeCount", comment.getLikeCount());		
//	
//		Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
//		comment.setId(key.longValue());
		
		jdbcTemplate.update("insert into TB_comment(body, boardId, writeDate, likeCount, userName, password)"
				+ "values(?, ?, ?, ?, ?, ?)", comment.getBody(), comment.getBoardId(), comment.getWriteDate(), comment.getLikeCount(), comment.getUserName(), comment.getPassword() ); 
		return comment;
	}
	
	public void update(Long id, String body) {
		jdbcTemplate.update("update TB_comment set body=? where id=?", body, id);
	}
	
	public List<Comment> findByAllComments(Long id) {
		return jdbcTemplate.query("select * from TB_comment where boardId= ?", commentRowMapper(), id);
	}
	
	public Optional<Comment> findByComment(Long id) {
		List<Comment> findComment = jdbcTemplate.query("select * from TB_comment where id = ?", commentRowMapper(), id);
		return findComment.stream().findAny();
	}
	
	public void delete(Long id) {
		jdbcTemplate.update("delete from TB_comment where id = ? ", id);
	}
	
	public void updateLikeCount(Long id) {
		jdbcTemplate.update("update TB_comment set likeCount=likeCount+1 where id = ?", id);
	}
	
	private RowMapper<Comment> commentRowMapper() {
		return (rs, rowNum) -> {
			Comment com = new Comment();
			com.setId(rs.getLong("id"));
			com.setBody(rs.getString("body"));
			com.setBoardId(rs.getLong("boardId"));
			com.setWriteDate(rs.getDate("writeDate"));
			com.setUserName(rs.getString("userName"));
			com.setPassword(rs.getString("password"));
			com.setLikeCount(rs.getInt("LikeCount"));
			
			return com;
		};
	}
	
}

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

public class JdbcBoardRepository {
	private final JdbcTemplate jdbcTemplate;
	private final UserRepository userRepository;
	
	public JdbcBoardRepository(DataSource dataSource, UserRepository userRepository) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		this.userRepository = userRepository;
	}
	
	public Board save(Board board) {
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
		jdbcInsert.withTableName("TB_board").usingGeneratedKeyColumns("id");
		Map<String,  Object> parameters = new HashMap<>();
		parameters.put("title", board.getTitle());
		parameters.put("body", board.getBody());
		parameters.put("writeDate", board.getWriteDate());
		parameters.put("userId", board.getUserId());
		parameters.put("viewCount", board.getViewCount());
		parameters.put("likeCount", board.getLikeCount());
		
		Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters)); //PK값 얻기
		board.setId(key.longValue());
		return board;
	}
	
	public void delete(Long id) {
		jdbcTemplate.update("delete from TB_board where id = ?", id);
	}
	
	public void modify(Board board, Long id) {
		jdbcTemplate.update("update TB_board set title=?, body=? where id=?", 
				board.getTitle(), board.getBody(), id);
	}
	
	public Optional<Board> findByBoard(Long id) {
		List<Board> result = jdbcTemplate.query("select * from TB_board where id = ?", 
																		boardRowMapper(), id);
		return result.stream().findAny();
	}
	
	public List<Board> findByBoardPage(int offset) {
		return jdbcTemplate.query("select * from TB_board order by writeDate desc, id desc limit 10 offset ?  ", boardRowMapper(), offset);
	}

	public List<Board> findByBoards() {
		return jdbcTemplate.query("select * from TB_board", boardRowMapper());
	}
	
	public void setViewCount(Long id) {
		jdbcTemplate.update("update TB_board set viewCount=viewCount+1 where id = ?", id);
	}
	
	public void setLikeCount(Long id) {
		jdbcTemplate.update("update TB_board set likeCount=likeCount+1 where id = ?", id);
	}
	
	private RowMapper<Board> boardRowMapper() {
		return (rs, rowNum) -> {
			Board board = new Board();
			board.setId(rs.getLong("id"));
			board.setTitle(rs.getString("title"));
			board.setBody(rs.getString("body"));
			board.setWriteDate(rs.getDate("writeDate"));
			board.setUserId(rs.getLong("userId"));
			board.setViewCount(rs.getInt("viewCount"));
			board.setLikeCount(rs.getInt("LikeCount"));
			
			return board;
		};
	}
	
}

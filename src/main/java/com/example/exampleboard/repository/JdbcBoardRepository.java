package com.example.exampleboard.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.core.io.buffer.LimitedDataBufferList;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.example.exampleboard.model.Board;
import com.example.exampleboard.model.User;

public class JdbcBoardRepository {
	private final JdbcTemplate jdbcTemplate;
	private final JdbcUserRepository jdbcUserRepository;
	
	public JdbcBoardRepository(DataSource dataSource, JdbcUserRepository jdbcUserRepository) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		this.jdbcUserRepository = jdbcUserRepository;
	}
	
	public Board save(Board board) {
//		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
//		jdbcInsert.withTableName("TB_board").usingGeneratedKeyColumns("id");
//		Map<String,  Object> parameters = new HashMap<>();
//		parameters.put("title", board.getTitle());
//		parameters.put("body", board.getBody());
//		parameters.put("writeDate", board.getWriteDate());
//		parameters.put("userId", board.getUserId());
//		parameters.put("viewCount", board.getViewCount());
//		parameters.put("likeCount", board.getLikeCount());
//		
//		Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters)); //PK값 얻기
//		board.setId(key.longValue());
//		return board;
		
		// 게시글 작성 insert
		jdbcTemplate.update("insert into TB_board(title, body, writeDate, userId, viewCount, likeCount) "
				+ "values(?, ?, ?, ?, ?, ?)", 
				board.getTitle(),board.getBody(), board.getWriteDate(), board.getUserId(), board.getViewCount(), board.getLikeCount());
		
		return board;
	}
	
	// 게시글 삭제 delete
	public void delete(Long id) {
		jdbcTemplate.update("delete from TB_board where id = ?", id);
	}
	
	// 게시글 수정 update
	public void modify(Board board, Long id) {
		jdbcTemplate.update("update TB_board set title=?, body=? where id=?", 
				board.getTitle(), board.getBody(), id);
	}
	
	// 게시글번호를 통해 게시글 찾기
	public Optional<Board> findByBoard(Long id) {
		List<Board> result = jdbcTemplate.query("select * from TB_board where id = ?", 
																		boardRowMapper(), id);
		return result.stream().findAny();
	}
	
	// 받아온 offset을 기준으로 10개의 데이터만 출력
	public List<Board> findByBoardPage(int offset) {
		return jdbcTemplate.query("select * from TB_board order by writeDate desc, id desc limit 10 offset ?  ", boardRowMapper(), offset);
	}

	// 모든 게시글 데이터 받아오기
	public List<Board> findByBoards() {
		return jdbcTemplate.query("select * from TB_board", boardRowMapper());
	}
	
	// 조회수 증가
	public void setViewCount(Long id) {
		jdbcTemplate.update("update TB_board set viewCount=viewCount+1 where id = ?", id);
	}
	
	// 좋아요 수 증가
	public void setLikeCount(Long id) {
		jdbcTemplate.update("update TB_board set likeCount=likeCount+1 where id = ?", id);
	}
	
	// 검색어에 해당하는 게시글 모두 받아와서 사이즈 출력
	public int findByKeywordSize(String selected, String keyword) {
		List<Board> findBoards = new ArrayList<>();
		Long findUserID = null;
		
		String wildCard ="%" + keyword + "%";
//		System.out.println(wildCard);
		if(selected.equals("제목"))
			findBoards = jdbcTemplate.query("select * from TB_board where title like ? ", boardRowMapper(), wildCard);
		else if(selected.equals("내용"))
			findBoards = jdbcTemplate.query("select * from TB_board where body like ? ", boardRowMapper(), wildCard);
		else if(selected.equals("작성자")) {
			if(jdbcUserRepository.findByName(keyword).isEmpty()) findUserID=0L;
			else findUserID = jdbcUserRepository.findByName(keyword).get().getId();
			findBoards = jdbcTemplate.query("select * from TB_board where userId = ? ", boardRowMapper(), findUserID);
		} else {
			return 0;
		}
		return findBoards.size();
	}
	
	// offset을 기준으로 10개의 검색어 게시글만 받아오기
	public List<Board> findByKeyword(String selected, String keyword, int offset) {
		List<Board> findBoards = new ArrayList<>();
//		System.out.println(keyword);
		String wildCard ="%" + keyword + "%";
		Long findUserID = null;
		
		if(selected.equals("제목"))
			findBoards = jdbcTemplate.query("select * from TB_board where title like ? order by writeDate desc, id desc limit 10 offset ?", boardRowMapper(), wildCard, offset);
		else if(selected.equals("내용"))
			findBoards = jdbcTemplate.query("select * from TB_board where body like ? order by writeDate desc, id desc limit 10 offset ?", boardRowMapper(), wildCard, offset);
		else if(selected.equals("작성자")) {
			if(jdbcUserRepository.findByName(keyword).isEmpty()) findUserID=0L;
			else findUserID = jdbcUserRepository.findByName(keyword).get().getId();
			findBoards = jdbcTemplate.query("select * from TB_board where userId = ? order by writeDate desc, id desc limit 10 offset ?", boardRowMapper(), findUserID, offset);
		} 
		else {
			return null;
		}
		
		return findBoards;
	}
	
	// 추출한 데이터 받아와서 객체에 넣기
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

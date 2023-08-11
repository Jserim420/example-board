package com.example.exampleboard.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.example.exampleboard.model.Board;
import com.example.exampleboard.repository.JdbcBoardRepository;

@Transactional
public class BoardService {
	

	private final JdbcBoardRepository boardRepository;
	

	public BoardService(JdbcBoardRepository boardRepository) {
		this.boardRepository = boardRepository;
	}

	public void write(Board board) {
		boardRepository.save(board);
	}
	
	public List<Board> findAllBoards() {
		return boardRepository.findByBoards();
	}
	
	public Board findBoard(Long boardId) {
		return boardRepository.findByBoard(boardId).get();
	}
	
	public List<Board> findBoardPage(int pageNum, int pageSize) {
		int offset = (pageNum - 1) * pageSize;
		System.out.println("offset : " + offset);
		return boardRepository.findByBoardPage(offset);
	}
	
	public List<Board> findKeywordPage(String selected, String keyword, int pageNum, int pageSize) {
		int offset = (pageNum - 1) * pageSize;
		System.out.println("offset : " + offset);
		return boardRepository.findByKeyword(selected, keyword, offset);
	}
	
	
	
	public void remove(Long id) {
		boardRepository.delete(id);
	}
	
	public void update(Board board, Long id) {
		boardRepository.modify(board, id);
	}
	
	public void updateViewCount(Long id) {
		boardRepository.setViewCount(id);
	}
	
	public void updateLikeCount(Long id) {
		boardRepository.setLikeCount(id);
	}
	
	public int keywordSize(String selected, String keyword) {
		return boardRepository.findByKeywordSize(selected,keyword);
	}
}
	


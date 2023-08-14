package com.example.exampleboard.controller;


import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exampleboard.AlertMessage;
import com.example.exampleboard.model.Board;
import com.example.exampleboard.model.Comment;
import com.example.exampleboard.model.PageDto;
import com.example.exampleboard.model.Pagination;
import com.example.exampleboard.model.User;
import com.example.exampleboard.repository.UserRepository;
import com.example.exampleboard.service.BoardService;
import com.example.exampleboard.service.CommentService;
import com.example.exampleboard.service.UserService;

import groovyjarjarantlr4.v4.parse.ANTLRParser.finallyClause_return;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class BoardController {
	
	private final UserRepository userRepository;
	private final BoardService boardService;
	private final CommentService commentService;
	
	public BoardController(UserRepository userRepository, BoardService boardService,CommentService commentService) {
		this.userRepository = userRepository;
		this.boardService = boardService;
		this.commentService = commentService;
	}

	
	@GetMapping("/board/write")
	public String boardWrite(@CookieValue(name = "userId", required = false) Long userId, Model model, User user, HttpServletResponse response) throws Exception {
		System.out.println(userId);
		if (userId != null) {  
			User loginUser = userRepository.findById(userId).get();
			model.addAttribute("user", loginUser);
			model.addAttribute("board", null);
			return "board/boardWrite";
		}
		else  { 
			AlertMessage.alertAndBack(response, "로그인을 해야 글을 작성하실 수 있습니다.");
			model.addAttribute("user", null);
		}

		return "redirect:/";
	
	}
	

	@PostMapping("/api/board/write")
	public void boardSave(@CookieValue(name = "userId", required = false) Long userId, 
			HttpServletResponse response,
			Model model, BoardForm boardForm) throws Exception {
		Board board = new Board();
		board.setTitle(boardForm.getTitle());
		board.setBody(boardForm.getBody());
		board.setUserId(userId);
		board.setWriteDate(new Date());
		board.setViewCount(0);
		board.setLikeCount(0);
		if(boardForm.getTitle().equals("")) AlertMessage.alertAndBack(response, "제목을 입력해주세요.");
		else if(boardForm.getBody().equals("")) AlertMessage.alertAndBack(response, "내용을 입력해주세요.");
		else {
			boardService.write(board);
			AlertMessage.alertAndMove(response, "글이 성공적으로 저장되었습니다.", "/");
		}
	}
	
	@GetMapping("/board")
	public String boardView(@RequestParam(name = "boardNo") Long id, @CookieValue(name = "userId", required = false) Long userId, Model model) {
		if (userId != null) { 
			User loginUser = userRepository.findById(userId).get();
			model.addAttribute("user", loginUser);
		}
		else  model.addAttribute("user", null);
		
		// 조회수 증가
		boardService.updateViewCount(id);
				
		// 게시글 찾기
		Board findBoards = boardService.findBoard(id);
		model.addAttribute("board", findBoards);
		String writerName = userRepository.findById(findBoards.getUserId()).get().getName();
		model.addAttribute("writer", writerName);
		
		
		// 댓글 찾기
		List<Comment> comments = commentService.findByAllComments(findBoards.getId());
		model.addAttribute("comments", comments);
		
		
	    return "board/boardView";
	}
	
	

	@GetMapping("/board/delete")
	public String delete(@RequestParam(name="boardNo") Long id) {
		boardService.remove(id);
		
		return "redirect:/";
	}
	
	@GetMapping("/board/modify")
	public String modify(@CookieValue(name="userId") Long userId, Model model,
			@RequestParam(name="boardNo") Long id) {
		Board findBoard = boardService.findBoard(id);
		model.addAttribute("board", findBoard);
		model.addAttribute("user", userRepository.findById(userId).get());
		
		return "board/boardWrite";
	}
	
	@PostMapping("/api/board/update")
	public String update(@RequestParam(name="boardNo") Long id, Board board, BoardForm boardForm, HttpServletResponse response) throws Exception {
		Board updateBoard = new Board();
		updateBoard.setTitle(boardForm.getTitle());
		updateBoard.setBody(boardForm.getBody());
		if(boardForm.getTitle().equals("")) AlertMessage.alertAndBack(response, "제목을 입력해주세요.");
		else if(boardForm.getBody().equals("")) AlertMessage.alertAndBack(response, "내용을 입력해주세요.");
		else {

			boardService.update(updateBoard, id);
			AlertMessage.alertAndMove(response, "글이 성공적으로 수정되었습니다.", "/");
		}
		return "redirect:/board?boardNo=" + id;
	}
	
	@GetMapping("/api/board/like")
	public String updateLike(@RequestParam(name="boardNo") Long id, Board board, BoardForm boardForm, HttpServletResponse response) throws Exception {
		boardService.updateLikeCount(id);
		return "redirect:/board?boardNo=" + id;
	}
	
	@GetMapping("/board/search")
	public String search(@CookieValue(name="userId",  required = false) Long userId,
			Model model,
			@RequestParam(name="selected") String selected,
			@RequestParam(name="keyword") String keyword,
			@RequestParam(name="pageNum", defaultValue = "1") int pageNum,
			@RequestParam(defaultValue = "10") int pageSize,
			HttpServletResponse response) throws Exception {
		
		 if (userId != null) {
		        User loginUser = userRepository.findById(userId).orElse(null);
		        model.addAttribute("user", loginUser);
		    } else {
		        model.addAttribute("user", null);
		    }
		 
		 if(keyword.equals("")) AlertMessage.alertAndBack(response, "검색어를 입력해주세요.");
		 else {
		    int totalData = boardService.keywordSize(selected, keyword);
		    System.out.println("사이즈 : " + totalData);
		    Pagination pagination = new Pagination(pageNum, pageSize);
		    
		    System.out.println("페이지 당 글 개수 : " + pagination.getAmount());
		    List<Board> boards = boardService.findKeywordPage(selected, keyword, pageNum, pagination.getAmount());
		    
		    PageDto pageDto = new PageDto(pagination, totalData);
		    model.addAttribute("pageDto", pageDto);
		    System.out.println(pageDto.toString());
		    model.addAttribute("boards",boards);
		    model.addAttribute("keyword", keyword);
		    model.addAttribute("selected", selected);
		 }
		    return "board/boardList";
	}
}
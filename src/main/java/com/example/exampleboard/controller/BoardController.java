package com.example.exampleboard.controller;



import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exampleboard.AlertMessage;
import com.example.exampleboard.model.Board;
import com.example.exampleboard.model.Comment;
import com.example.exampleboard.model.User;
import com.example.exampleboard.service.BoardService;
import com.example.exampleboard.service.CommentService;
import com.example.exampleboard.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class BoardController {
	
	private final BoardService boardService;
	private final UserService userService;
	private final CommentService commentService;
	
	@Autowired
	public BoardController(BoardService boardService, UserService userService, CommentService commentService) {
		this.boardService = boardService;
		this.userService = userService;
		this.commentService = commentService;
	}

	
	
	// 글쓰기
	@GetMapping("/board/write")
	public String boardWrite(@CookieValue(name = "userId", required = false) Long userId, Model model, User user, HttpServletResponse response) throws Exception {
		System.out.println(userId);
		if (userId != null) {  // 사용자가 로그인 상태라면
			User loginUser = userService.findUser(userId);
			model.addAttribute("user", loginUser);
			model.addAttribute("board", null);
			return "board/boardWrite";
		}
		else  { // 사용자가 로그인 상태가 아니라면
			AlertMessage.alertAndBack(response, "로그인을 해야 글을 작성하실 수 있습니다.");
			model.addAttribute("user", null);
		}

		return "redirect:/";
	
	}
	
	// 글쓰기 로직
	@PostMapping("/api/board/write")
	public void boardSave(@CookieValue(name = "userId", required = false) Long userId, 
			HttpServletResponse response,
			Model model, BoardForm boardForm) throws Exception {
		Board board = new Board();
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		board.setTitle(boardForm.getTitle());
		board.setBody(boardForm.getBody());
		board.setUserId(userId);
		board.setWriteDate(simpleDateFormat.format(date));
		board.setViewCount(0);
		board.setLikeCount(0);
		// 제목,내용을 입력하지 않았을 때
		if(boardForm.getTitle().equals("")) AlertMessage.alertAndBack(response, "제목을 입력해주세요.");
		else if(boardForm.getBody().equals("")) AlertMessage.alertAndBack(response, "내용을 입력해주세요.");
		else {
			boardService.write(board);
			AlertMessage.alertAndMove(response, "글이 성공적으로 저장되었습니다.", "/");
		}
	}
	
	// 게시글 조회
	@GetMapping("/board")
	public String boardView(@RequestParam(name = "boardNo") Long id, 
			@CookieValue(name = "userId", required = false) Long userId, 
			Model model, HttpServletResponse response, HttpServletRequest request) {
		if (userId != null) { // 로그인한 사용자가 게시글 작성자인지 확인하기 위해  model 에 값 전달
			User loginUser = userService.findUser(userId);
			model.addAttribute("user", loginUser);
		}
		else  model.addAttribute("user", null);
		
		System.out.println("test");
		// 조회수 증가
		boardService.updateViewCount(id, request, response);
		System.out.println("test2");
		// 게시글 찾기
		Board findBoards = boardService.findBoard(id);
		System.out.println(findBoards.getViewCount());
		model.addAttribute("board", findBoards);
		String writerName = userService.findUser(findBoards.getUserId()).getName();
		model.addAttribute("writer", writerName);
		
		
		// 댓글 찾기
		List<Comment> comments = commentService.findByAllComments(findBoards.getId());
		model.addAttribute("comments", comments);
		
		
	    return "board/boardView";
	}

	// 게시글 수정
	@GetMapping("/board/modify")
	public String modify(@CookieValue(name="userId", required = false) Long userId, Model model,
			@RequestParam(name="boardNo") Long id, HttpServletResponse response) throws Exception {
		Board findBoard = boardService.findBoard(id);
		if(userId==null | userId!=findBoard.getUserId()) {
			AlertMessage.alertAndBack(response, "게시글 수정은 게시글 작성자만 할 수 있습니다.");
			return null;
		} else { 
			model.addAttribute("board", findBoard);
			model.addAttribute("user", userService.findUser(userId));
		}
		return "board/boardWrite";
	}

	// 게시글 수정 로직
	@PostMapping("/api/board/update")
	public String update(@RequestParam(name="boardNo") Long id, Board board, BoardForm boardForm, HttpServletResponse response) throws Exception {
		Board updateBoard = new Board();
		updateBoard.setTitle(boardForm.getTitle());
		updateBoard.setBody(boardForm.getBody());
		
		// 제목, 내용을 입력하지 않았을 때
		if(boardForm.getTitle().equals("")) AlertMessage.alertAndBack(response, "제목을 입력해주세요.");
		else if(boardForm.getBody().equals("")) AlertMessage.alertAndBack(response, "내용을 입력해주세요.");
		else {

			boardService.update(updateBoard, id);
			AlertMessage.alertAndMove(response, "글이 성공적으로 수정되었습니다.", "/");
		}
		return "redirect:/board?boardNo=" + id;
	}
	
//	
//	
	// 게시글 삭제
	@GetMapping("/board/delete")
	public String delete(@RequestParam(name="boardNo") Long id) {
		boardService.delete(id);
		
		return "redirect:/";
	}
//	
	
//	
//	
//	// 게시글 좋아요
//	@GetMapping("/api/board/like")
//	public String updateLike(@RequestParam(name="boardNo") Long id, Board board, BoardForm boardForm, HttpServletResponse response) throws Exception {
//		boardService.updateLikeCount(id);
//		return "redirect:/board?boardNo=" + id;
//	}
//	
//	// 게시글 검색
//	@GetMapping("/board/search")
//	public String search(@CookieValue(name="userId",  required = false) Long userId,
//			Model model,
//			@RequestParam(name="selected") String selected,
//			@RequestParam(name="keyword") String keyword,
//			@RequestParam(name="pageNum", defaultValue = "1") int pageNum,
//			@RequestParam(defaultValue = "10") int pageSize,
//			HttpServletResponse response) throws Exception {
//		
//		 if (userId != null) {
//		        User loginUser = jdbcUserRepository.findById(userId).orElse(null);
//		        model.addAttribute("user", loginUser);
//		    } else {
//		        model.addAttribute("user", null);
//		    }
//		 
//		 // 검색어를 입력하지 않았을 때
//		 if(keyword.equals("")) AlertMessage.alertAndBack(response, "검색어를 입력해주세요.");
//		 else { // 검색어를 입력했을때
//		    int totalData = boardService.keywordSize(selected, keyword);
////		    System.out.println("사이즈 : " + totalData);
//		    Pagination pagination = new Pagination(pageNum, pageSize);
//		    
////		    System.out.println("페이지 당 글 개수 : " + pagination.getAmount());
//		    List<Board> boards = boardService.findKeywordPage(selected, keyword, pageNum, pagination.getAmount());
//		    
//		    PageDto pageDto = new PageDto(pagination, totalData);
//		    model.addAttribute("pageDto", pageDto);
////		    System.out.println(pageDto.toString());
//		    model.addAttribute("boards",boards);
//		    model.addAttribute("keyword", keyword);
//		    model.addAttribute("selected", selected);
//		 }
//		    return "board/boardList";
//	}
}
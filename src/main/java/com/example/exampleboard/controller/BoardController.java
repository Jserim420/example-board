package com.example.exampleboard.controller;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import com.example.exampleboard.service.JwtProvider;
import com.example.exampleboard.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BoardController {
	
	private final BoardService boardService;
	private final UserService userService;
	private final CommentService commentService;
	private final JwtProvider jwtProvider;
	
	
	@Autowired
	public BoardController(BoardService boardService, UserService userService, 
			CommentService commentService, JwtProvider jwtProvider) {
		this.boardService = boardService;
		this.userService = userService;
		this.commentService = commentService;
		this.jwtProvider = jwtProvider;
	}

	
	
	// 글쓰기
	@GetMapping("/board/write")
	public String boardWrite(@CookieValue(name = "loginUser", required = false) String loginToken, Model model, User user, HttpServletResponse response) throws Exception {
		System.out.println(loginToken);
		if (loginToken != null) {  // 사용자가 로그인 상태라면
			Long userId = jwtProvider.getToken("Id", loginToken);
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
	public void boardSave(@CookieValue(name = "loginUser", required = false) String loginToken, 
			HttpServletResponse response,
			Model model, BoardForm boardForm) throws Exception {
		Board board = new Board();
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		board.setTitle(boardForm.getTitle());
		board.setBody(boardForm.getBody());
		board.setUserId(jwtProvider.getToken("Id", loginToken));
		board.setWriteDate(simpleDateFormat.format(date));
		board.setViewCount(0);
		board.setLikeCount(0);
		
		// 제목,내용을 입력하지 않았을 때
		if(boardForm.getTitle().equals("")) AlertMessage.alertAndBack(response, "제목을 입력해주세요.");
		else if(boardForm.getBody().equals("")) AlertMessage.alertAndBack(response, "내용을 입력해주세요.");
		else {
			try {
				boardService.write(board);
			} catch (Exception e) {
				log.warn("글작성 문제발생 => {} \n 작성자 [ 회원번호 : {} ] ", e.getMessage(), board.getUserId());
				AlertMessage.alertAndBack(response, "글 작성 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
			}
			log.info("글작성 완료 [ 제목 : {} \n 내용 : {} \n 작성자 회원번호 : {} ]", board.getTitle(), board.getBody(), board.getUserId());
			AlertMessage.alertAndMove(response, "글이 성공적으로 저장되었습니다.", "/");
		}
	}
	
	// 게시글 조회
	@GetMapping("/board")
	public String boardView(@RequestParam(name = "boardNo") Long boardId, 
			@CookieValue(name = "loginUser", required = false) String loginToken, 
			Model model, HttpServletResponse response, HttpServletRequest request) throws Exception {
		Long userId = null;
		Board findBoards = null;
		List<Comment> comments = null;
		String writerName = null;
		
		if (loginToken != null) { // 로그인한 사용자가 게시글 작성자인지 확인하기 위해  model 에 값 전달
			userId = jwtProvider.getToken("Id", loginToken);
			User loginUser = userService.findUser(userId);
			model.addAttribute("user", loginUser);
		}
		else  model.addAttribute("user", null);
		
		try {
			// 게시글 찾기
			findBoards = boardService.findBoard(boardId);
			// 조회수 증가
			boardService.updateCount(boardId, request, response, "view");
			// 댓글 찾기
			comments = commentService.findByAllComments(findBoards.getId());
			// 작성자 닉네임 찾기
			writerName = userService.findUser(findBoards.getUserId()).getName();
		} catch (Exception e) {
			log.warn("게시글 [ 번호 : {} ] 조회 문제발생 => {} \n 조회유저 [ 회원번호 : {} ]", boardId, e.getMessage(), userId);
			AlertMessage.alertAndBack(response, "게시글을 불러오는데 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
		}
		
		model.addAttribute("board", findBoards);
		model.addAttribute("writer", writerName);
		model.addAttribute("comments", comments);
		
		
	    return "board/boardView";
	}

	// 게시글 수정
	@GetMapping("/board/modify")
	public String modify(@CookieValue(name="loginUser", required = false)String loginToken, Model model,
			@RequestParam(name="boardNo") Long id, HttpServletResponse response) throws Exception {
		Board findBoard = boardService.findBoard(id);
		Long userId = jwtProvider.getToken("Id", loginToken);
		if(loginToken==null | userId!=findBoard.getUserId()) {
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
	public String update(@RequestParam(name="boardNo") Long boardId, Board board, BoardForm boardForm, 
			HttpServletResponse response, @CookieValue(name="userId", required = false) Long userId) throws Exception {
		if(userId!=boardService.findBoard(boardId).getUserId()) AlertMessage.alertAndBack(response, "게시글 수정은 게시글 작성자만 할 수 있습니다.");
		else {
			Board updateBoard = new Board();
			updateBoard.setTitle(boardForm.getTitle());
			updateBoard.setBody(boardForm.getBody());
			
			// 제목, 내용을 입력하지 않았을 때
			if(boardForm.getTitle().equals("")) AlertMessage.alertAndBack(response, "제목을 입력해주세요.");
			else if(boardForm.getBody().equals("")) AlertMessage.alertAndBack(response, "내용을 입력해주세요.");
			else {
				try {
					boardService.update(updateBoard, boardId);
				} catch (Exception e) {
					log.warn("게시글 [ 번호 : {} ] 수정 문제발생 => {} \n 수정유저 [ 회원번호 : {} ]", boardId, e.getMessage(), userId);
					AlertMessage.alertAndBack(response, "게시글 수정 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
				}
				
				log.info("게시글 [ 번호 : {} ] 수정 완료", boardId);
				AlertMessage.alertAndMove(response, "글이 성공적으로 수정되었습니다.", "/board?boardNo=" + boardId);
			}
		}
		return "redirect:/board?boardNo=" + boardId;
	}
	
//	
//	
	// 게시글 삭제
	@GetMapping("/board/delete")
	public String delete(@RequestParam(name="boardNo") Long boardId, @CookieValue(name="loginUser", required = false) String loginToken,
			HttpServletResponse response) throws Exception {
		if(jwtProvider.getToken("Id", loginToken)!=boardService.findBoard(boardId).getUserId()) 
			AlertMessage.alertAndBack(response, "게시글 삭제는 게시글 작성자만 할 수 있습니다.");
		else {
			try {
				boardService.delete(boardId);
			} catch (Exception e) {
				log.warn("게시글 [ 번호 : {} ] 삭제 문제발생 => {} \n 삭제유저 [ 회원번호 : {} ]", boardId, e.getMessage(), jwtProvider.getToken("Id", loginToken));
				AlertMessage.alertAndBack(response, "게시글 삭제 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
			}
			
			log.info("게시글 [ 번호 : {} ] 삭제완료", boardId);
			AlertMessage.alertAndBack(response, "게시글이 삭제되었습니다.");
		}
		
		return "redirect:/";
	}
	
	// 게시글 좋아요
	@GetMapping("/api/board/like")
	public String updateLike(@RequestParam(name="boardNo") Long boardId, Board board, BoardForm boardForm, 
			@CookieValue(name="loginUser", required = false) String loginToken,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		System.out.println("좋아요");
		if(loginToken==null) boardService.updateCount(boardId, request, response, "like");
		else {
			if(jwtProvider.getToken("Id", loginToken)==boardService.findBoard(boardId).getUserId()) 
				AlertMessage.alertAndBack(response, "내 글에는 좋아요를 누를 수 없습니다.");
			else {
				try {
				boardService.updateCount(boardId, request, response, "like");
				} catch (Exception e) {
					log.warn("게시글 [ 번호 : {} ] 좋아요수 증가 문제발생 => {} \n 유저 [ 회원번호 : {} ]", boardId, e.getMessage(), jwtProvider.getToken("Id", loginToken));
					AlertMessage.alertAndBack(response, "문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
				}
				
				log.info("게시글 [ 번호 : {} ] 좋아요수 증가 {}", boardId, boardService.findBoard(boardId).getLikeCount());
			}
		}
		return "redirect:/board?boardNo=" + boardId;
	}
	
//	
	
//	
	// 게시글 검색
	@GetMapping("/board/search")
	public String search(@CookieValue(name="loginUser",  required = false) String loginToken,
			Model model,
			@RequestParam(name="selected") String selected,
			@RequestParam(name="keyword") String keyword,
			@PageableDefault(page=0, size=10, sort="writeDate", direction = Sort.Direction.DESC) Pageable pageable,
			HttpServletResponse response) throws Exception {
		Page<Board> searchList = null;
		
		if(keyword.equals("")) AlertMessage.alertAndBack(response, "검색어를 입력해주세요.");
		else if(selected.equals("")) AlertMessage.alertAndBack(response, "검색범위를 설정해주세요");
		else {	
			model.addAttribute("user", userService.findUser(jwtProvider.getToken("Id", loginToken)));
		    model.addAttribute("keyword", keyword);
		    model.addAttribute("selected", selected);
		    try {
		    	if(selected.equals("작성자")) {
					if(userService.searchName(keyword).isPresent()) 
						keyword=Long.toString(userService.searchName(keyword).get().getId());
					else { 
						AlertMessage.alertAndBack(response, "해당 유저는 존재하지 않습니다.");
						return "board/boardList";
					}
				} 
		    	
				searchList = boardService.search(selected, keyword, pageable);
			} catch (Exception e) {
				log.warn("게시글 검색 문제 발생 => {} \n 검색어 [ 검색옵션 : {} , 키워드 : {} ]", e.getMessage(), selected, keyword);
				AlertMessage.alertAndBack(response, "문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
			}
			
		    log.info("검색어 [ 검색옵션 : {} , 키워드 : {} ] 조회 \n"
		    		+ "=> [ 총 element 수 : {} , 전체 page 수 : {} , 페이지에 표시할 element 수 : {} , 현재 페이지 index : {}, 현재 페이지의 element 수 : {} ]",
		    		selected, keyword,
		    		searchList.getTotalElements(), searchList.getTotalPages(), searchList.getSize(), searchList.getNumber(), searchList.getNumberOfElements() );
		    
			if(searchList.getTotalElements()<1 | searchList==null) model.addAttribute("boards", null);
			else model.addAttribute("boards", searchList);
		}
		    
			return "board/boardList";
	}
	
}
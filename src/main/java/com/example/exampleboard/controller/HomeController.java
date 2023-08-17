package com.example.exampleboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exampleboard.model.Board;
import com.example.exampleboard.model.PageDto;
import com.example.exampleboard.model.Pagination;
import com.example.exampleboard.model.User;
import com.example.exampleboard.repository.JdbcUserRepository;
import com.example.exampleboard.service.BoardService;
import com.example.exampleboard.service.UserService;

@Controller
public class HomeController {
	
	private final JdbcUserRepository jdbcUserRepository;
	private final BoardService boardService;
	
	@Autowired
	public HomeController(JdbcUserRepository jdbcUserRepository, BoardService boardService) {
		this.jdbcUserRepository=jdbcUserRepository;
		this.boardService = boardService;
	}
	
	// WelcomePage
	@GetMapping("/")
	public String boardList(@CookieValue(name = "userId", required = false) Long userId, Model model,
	                        @RequestParam(defaultValue = "1") int pageNum,
	                        @RequestParam(defaultValue = "10") int pageSize) {
	    System.out.println(userId);
	    
	    if (userId != null) {
	        User loginUser = jdbcUserRepository.findById(userId).orElse(null);
	        model.addAttribute("user", loginUser);
	    } else {
	        model.addAttribute("user", null);
	    }

	    // 전체 게시글 사이즈 확인
	    int totalData = boardService.findAllBoards().size(); 
//	    System.out.println("사이즈 : " + totalData);
	    // (default 페이지 번호는 1쪽, 사이즈는 10개 => 한페이지당 10개의 게시글)
	    Pagination pagination = new Pagination(pageNum, pageSize);
	    
//	    System.out.println("페이지 당 글 개수 : " + pagination.getAmount());
	    List<Board> boards = boardService.findBoardPage(pageNum, pagination.getAmount());
	    
	    PageDto pageDto = new PageDto(pagination, totalData);
	    model.addAttribute("pageDto", pageDto);
	    System.out.println(pageDto.toString());
	    model.addAttribute("boards",boards);

	    return "board/boardList";
	}


}

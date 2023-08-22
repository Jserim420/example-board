package com.example.exampleboard.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.exampleboard.model.Board;
import com.example.exampleboard.model.User;
import com.example.exampleboard.service.BoardService;
import com.example.exampleboard.service.UserService;





@Controller
public class HomeController {
	
	private final UserService userService;
	private final BoardService boardService;

	@Autowired
	public HomeController(UserService userService, BoardService boardService) {
		this.userService = userService;
		this.boardService = boardService;
	}
	
	
	// WelcomePage
	@GetMapping("/")
	public String boardList(@CookieValue(name = "userId", required = false) Long userId, Model model,
							@PageableDefault(page=0, size=10, sort="writeDate", direction = Sort.Direction.DESC) Pageable pageable) {
	    System.out.println("로그인한 사용자 : " + userId);
	    
	   
	    if (userId != null) {
	        User loginUser = userService.findUser(userId);
	        model.addAttribute("user", loginUser);
	    } else {
	        model.addAttribute("user", null);
	    }
	    
	    Page<Board> boardList = boardService.boardList(pageable);
//	    System.out.println("총 element 수 :" +  boardList.getTotalElements() + " 전체 page 수 : " + boardList.getTotalPages()
//	    							+ " 페이지에 표시할 element 수 : " + boardList.getSize() + " 현재 페이지 index : " +  boardList.getNumber() 
//	    							+ " 현재 페이지의 element 수 : " + boardList.getNumberOfElements());
	    
	    model.addAttribute("boards", boardList);

	    return "board/boardList";
	}
	
	


}

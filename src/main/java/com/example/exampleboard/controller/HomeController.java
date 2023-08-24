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
import com.example.exampleboard.service.JwtProvider;
import com.example.exampleboard.service.UserService;





@Controller
public class HomeController {
	
	private final UserService userService;
	private final BoardService boardService;
	private final JwtProvider jwtProvider;

	@Autowired
	public HomeController(UserService userService, BoardService boardService, JwtProvider jwtProvider) {
		this.userService = userService;
		this.boardService = boardService;
		this.jwtProvider = jwtProvider;
	}
	
	
	// WelcomePage
	@GetMapping("/")
	public String boardList(@CookieValue(name = "loginUser", required = false) String loginToken, Model model,
							@PageableDefault(page=0, size=10, sort="writeDate", direction = Sort.Direction.DESC) Pageable pageable) {

		if(loginToken==null) model.addAttribute("user", null);
		else {
			System.out.println("로그인한 사용자토큰 : " + loginToken);
		    System.out.println(jwtProvider.getToken("Id", loginToken));
		    User loginUser = userService.findUser(jwtProvider.getToken("Id", loginToken));
		    model.addAttribute("user", loginUser);
		}
	    
	    Page<Board> boardList = boardService.boardList(pageable);
//	    System.out.println("총 element 수 :" +  boardList.getTotalElements() + " 전체 page 수 : " + boardList.getTotalPages()
//	    							+ " 페이지에 표시할 element 수 : " + boardList.getSize() + " 현재 페이지 index : " +  boardList.getNumber() 
//	    							+ " 현재 페이지의 element 수 : " + boardList.getNumberOfElements());
	    
	    model.addAttribute("boards", boardList);

	    return "board/boardList";
	}
	
	


}

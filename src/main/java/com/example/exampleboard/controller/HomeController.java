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

import lombok.extern.slf4j.Slf4j;




@Slf4j
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

		if(loginToken==null) {
			log.info("로그인 유저 => [ 비회원 ]");
			model.addAttribute("user", null);
		}
		else {
		    User loginUser = userService.findUser(jwtProvider.getToken("Id", loginToken));
		    log.info("로그인 유저 => [ 토큰 : {} , 아이디 : {} , 닉네임 : {} ]" , 
		    		loginToken, loginUser.getEmail(), loginUser.getName() );
		    model.addAttribute("user", loginUser);
		}
	    
	    Page<Board> boardList = boardService.boardList(pageable);
	    log.info("페이징 조회 => [ 총 element 수 : {} , 전체 page 수 : {} , 페이지에 표시할 element 수 : {} , 현재 페이지 index : {}, 현재 페이지의 element 수 : {} ]",
	    		boardList.getTotalElements(), boardList.getTotalPages(), boardList.getSize(), boardList.getNumber(), boardList.getNumberOfElements() );
	    
	    model.addAttribute("boards", boardList);

	    return "board/boardList";
	}
	
	


}

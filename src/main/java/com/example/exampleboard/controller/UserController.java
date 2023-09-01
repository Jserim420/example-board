package com.example.exampleboard.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.exampleboard.AlertMessage;
import com.example.exampleboard.model.User;
import com.example.exampleboard.service.JwtProvider;
import com.example.exampleboard.service.UserService;

import groovyjarjarantlr4.v4.parse.ANTLRParser.finallyClause_return;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserController {
	
	private final UserService userService;
	private final JwtProvider jwtProvider;
	
	@Autowired
	public UserController(UserService userService, JwtProvider jwtProvider) {
		this.userService = userService;
		this.jwtProvider = jwtProvider;
	}
	
	// 로그아웃
	@GetMapping(value="/user/logout")
	public String logout(HttpServletResponse response, @CookieValue(name = "loginUser") String loginToken) {
		if(loginToken==null) log.warn("잘못된 접근 : 로그인사용자 없음");
		else { 
			Cookie cookie = new Cookie("loginUser", null); // 쿠키삭제
			cookie.setMaxAge(0); 
			cookie.setPath("/");
			response.addCookie(cookie);
			log.info("로그아웃 사용자 [ 아이디 : {} ]", jwtProvider.getToken("id", loginToken));
		}
		return "redirect:/";
	}
	
	// 회원가입 폼
	@GetMapping(value = "/user/signup")
	public String createForm() {
		return "signup";
	}
	
	// 회원가입 로직
	@PostMapping(value = "/api/user/signup")
	public void create(UserForm form, Model model, HttpServletResponse response) throws Exception {
		User user = new User();
		user.setEmail(form.getEmail());
		user.setPassword(form.getPassword());
		user.setName(form.getName());
		
		if(form.getName().equals("")) AlertMessage.alertAndBack(response, "이름을 입력해주세요.");
		else if(form.getEmail().equals("")) AlertMessage.alertAndBack(response, "아이디를 입력해주세요.");
		else if(form.getPassword().equals("")) AlertMessage.alertAndBack(response, "비밀번호를 입력해주세요.");
		else if(form.getPassword().length()<4) AlertMessage.alertAndBack(response, "비밀번호를 4글자 이상 입력해주세요.");
		else if(!form.getPassword().equals(form.getPasswordConfirm())) AlertMessage.alertAndBack(response, "비밀번호가 일치하지 않습니다.");
		else if(!form.getEmail().contains("@")) AlertMessage.alertAndBack(response, "올바르지 않은 이메일 형식입니다.");
		else {
			if(userService.isName(user)!=null) AlertMessage.alertAndBack(response, "이미 존재하는 닉네임입니다.");
			else if(userService.isEmail(user)!=null) AlertMessage.alertAndBack(response, "이미 가입된 이메일입니다.");
			else {
				try {
					userService.join(user);
				} catch(Exception e) {
					log.warn("회원가입 문제 발생 => {}", e.getMessage());
					AlertMessage.alertAndBack(response, "회원가입 도중 예상치 못한 문제가 발생하였습니다. 잠시 후 다시 시도해 주세요.");
				} 
				
				AlertMessage.alertAndMove(response, "회원가입에 성공했습니다.", "/login"); 
				
			}
		}
	}
	
	// 로그인
	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}
	

	// 로그인 로직
	@PostMapping("/api/user/login")
	public void login(UserForm form, HttpServletResponse response, Exception e) throws Exception {
		User user = new User();
		user.setEmail(form.getEmail());
		user.setPassword(form.getPassword());
		
		if(user.getEmail().equals("")) AlertMessage.alertAndBack(response, "아이디를 입력하세요.");
		else if(user.getPassword().equals("")) AlertMessage.alertAndBack(response, "비밀번호를 입력하세요");
		else if(user.getPassword().length()<4) AlertMessage.alertAndBack(response, "비밀번호를 4글자 이상 입력해주세요.");
		// 아이디 비밀번호 불일치
		else if(!user.getEmail().contains("@")) AlertMessage.alertAndBack(response, "올바르지 않은 이메일 형식입니다.");
		else if(userService.checkUser(user)==false || userService.isEmail(user)==null)  AlertMessage.alertAndBack(response, "아이디/비밀번호가 일치하지 않습니다.");
		else {
			//로그인 검증
			User findUser=null;
			String createToken=null;
			try {
				findUser = userService.isEmail(user);
				createToken = jwtProvider.generateToken(findUser);
			} catch (Exception e1) {
				log.warn("사용자 [ 아이디 : {}, 비밀번호 : {} ] 로그인 문제 발생 => {}", user.getEmail(), user.getPassword(), e1.getMessage());
				AlertMessage.alertAndBack(response, "로그인 도중 예상치 못한 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
			}
			
				Cookie cookie = new Cookie("loginUser", createToken);
				cookie.setMaxAge(10 * 60);
				cookie.setPath("/");
				cookie.setHttpOnly(true);	
				cookie.setSecure(true);
				response.addCookie(cookie);
				
				log.info("사용자 로그인 완료 : {}", user.getEmail());
				AlertMessage.alertAndMove(response, "어서오세요 :)", "/");
			
		}
	}
	
	
}

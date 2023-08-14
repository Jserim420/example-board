package com.example.exampleboard.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.unbescape.css.CssIdentifierEscapeLevel;

import com.example.exampleboard.AlertMessage;
import com.example.exampleboard.model.User;
import com.example.exampleboard.repository.UserRepository;
import com.example.exampleboard.service.UserService;

import ch.qos.logback.core.joran.conditional.ElseAction;
import groovyjarjarantlr4.v4.parse.ANTLRParser.finallyClause_return;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {
	
	private final UserService userService;
	private final UserRepository userRepository;
	
	@Autowired
	public UserController(UserService userService, UserRepository userRepository) {
		this.userService = userService;
		this.userRepository = userRepository;
	}
	
	@GetMapping(value="/user/logout")
	public String logout(HttpServletResponse response) {
		Cookie cookie = new Cookie("userId", null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
		return "redirect:/";
	}
	
	@GetMapping(value = "/user/signup")
	public String createForm() {
		return "signup";
	}
	
	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}
	
	
	@PostMapping(value = "/api/user/signup")
	public void create(UserForm form, Model model, HttpServletResponse response) throws Exception {
		User user = new User();
		user.setEmail(form.getEmail());
		user.setPassword(form.getPassword());
		user.setName(form.getName());
		
		if(form.getName().equals("")) AlertMessage.alertAndBack(response, "이름을 입력해주세요.");
		else if(form.getEmail().equals("")) AlertMessage.alertAndBack(response, "아이디를 입력해주세요.");
		else if(form.getPassword().equals("")) AlertMessage.alertAndBack(response, "비밀번호를 입력해주세요.");
		else if(!form.getPassword().equals(form.getPasswordConfirm())) AlertMessage.alertAndBack(response, "비밀번호가 일치하지 않습니다.");
		else {
			if(userService.isName(user)==true) AlertMessage.alertAndBack(response, "이미 존재하는 닉네임입니다.");
			else if(userService.isUser(user)==true) AlertMessage.alertAndBack(response, "이미 가입된 이메일입니다.");
			else AlertMessage.alertAndMove(response, "회원가입에 성공했습니다.", "/login"); 
		}
	}
	
	
	@PostMapping("/api/user/login")
	public void login(UserForm form, HttpServletResponse response, Exception e) throws Exception {
		User user = new User();
		user.setEmail(form.getEmail());
		user.setPassword(form.getPassword());
		
		User loginUser = userService.checkUser(user);
		Long loginId = userRepository.findByEmail(user.getEmail()).get().getId();
		// System.out.println(form.getEmail() + "," + form.getPassword());
		// 아이디 비밀번호 미입력
		if(user.getEmail().equals("")) AlertMessage.alertAndBack(response, "아이디를 입력하세요.");
		else if(user.getPassword().equals("")) AlertMessage.alertAndBack(response, "비밀번호를 입력하세요");
		// 아이디 비밀번호 불일치
		else if(loginUser==null)  AlertMessage.alertAndBack(response, "아이디/비밀번호가 일치하지 않습니다.");
		
		else {
			//로그인 성공
			System.out.println("로그인유저" + loginUser.getId());
			Cookie cookie = new Cookie("userId", String.valueOf(loginId));
			cookie.setMaxAge(30 * 60);
			cookie.setPath("/");
			response.addCookie(cookie);
			
			// System.out.println(cookie.getValue());
			AlertMessage.alertAndMove(response, "어서오세요 :)", "/");
		}
	}
	
	
}

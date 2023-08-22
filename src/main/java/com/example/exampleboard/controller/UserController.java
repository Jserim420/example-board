package com.example.exampleboard.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.exampleboard.AlertMessage;
import com.example.exampleboard.model.User;
import com.example.exampleboard.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {
	
	private final UserService userService;
	
	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	// 로그아웃
	@GetMapping(value="/user/logout")
	public String logout(HttpServletResponse response) {
		Cookie cookie = new Cookie("userId", null); // 쿠키삭제
		cookie.setMaxAge(0); 
		cookie.setPath("/");
		response.addCookie(cookie);
		return "redirect:/";
	}
	
	// 회원가입 폼
	@GetMapping(value = "/user/signup")
	public String createForm() {
		return "signup";
	}
	
	// 회원가입 로직
	@PostMapping(value = "/api/user/signup")
	public String create(UserForm form, Model model, HttpServletResponse response) throws Exception {
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
				userService.join(user);
				AlertMessage.alertAndMove(response, "회원가입에 성공했습니다.", "/login"); 
			}
		}
	
		return "login";
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
			//로그인 성공
			Long loginId = userService.isEmail(user).getId();
			System.out.println("로그인유저" + loginId);
			Cookie cookie = new Cookie("userId", String.valueOf(loginId));
			cookie.setMaxAge(10 * 60);
			cookie.setPath("/");
			response.addCookie(cookie);
			
			// System.out.println(cookie.getValue());
			AlertMessage.alertAndMove(response, "어서오세요 :)", "/");
		}
	}
	
	
}

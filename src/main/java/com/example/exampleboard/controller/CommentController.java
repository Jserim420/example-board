package com.example.exampleboard.controller;



import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exampleboard.AlertMessage;
import com.example.exampleboard.model.Board;
import com.example.exampleboard.model.Comment;
import com.example.exampleboard.service.CommentService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CommentController {

	private final CommentService commentService;
	
	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}
	
	@PostMapping("/api/comment/write")
	public void write(@CookieValue(name="userId", defaultValue = "") Long userId, 
			@RequestParam(name="boardNo") Long boardId,
			Board board, CommentForm commentForm,
			HttpServletResponse response) throws Exception  {
		Comment comment = new Comment();
		comment.setBody(commentForm.getBody());
		comment.setUserName(commentForm.getName());
		comment.setPassword(commentForm.getPassword());
		System.out.println(comment.getPassword());
		comment.setBoardId(boardId);
		comment.setLikeCount(0);
		comment.setWriteDate(new Date());
		
		if(commentForm.getBody()=="") {
			AlertMessage.alertAndMove(response, "댓글을 작성해주세요.",
					"/board?boardNo=" + boardId);
		}
		if(commentForm.getName()=="") {
			AlertMessage.alertAndMove(response, "작성자 닉네임을 입력해주세요.",
					"/board?boardNo=" + boardId);
		}
		if(commentForm.getPassword()=="") {
			AlertMessage.alertAndMove(response, "비밀번호를 입력해주세요.",
					"/board?boardNo=" + boardId);
		}
		else {
			commentService.write(comment);
			AlertMessage.alertAndMove(response, "댓글이 정상적으로 등록되었습니다.",
					"/board?boardNo=" + boardId);
		}
		
	}
}

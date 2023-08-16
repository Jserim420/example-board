package com.example.exampleboard.controller;



import java.io.IOException;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
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
		
		if(commentForm.getBody()=="")
			AlertMessage.alertAndMove(response, "내용을 작성해주세요.","/board?boardNo=" + boardId);
		else if(commentForm.getName()=="")
			AlertMessage.alertAndMove(response, "작성자 닉네임을 입력해주세요.", "/board?boardNo=" + boardId);
		else if(commentForm.getPassword()=="")
			AlertMessage.alertAndMove(response, "비밀번호를 입력해주세요.", "/board?boardNo=" + boardId);
		else {
			commentService.write(comment);
			AlertMessage.alertAndMove(response, "댓글이 정상적으로 등록되었습니다.",
					"/board?boardNo=" + boardId);
		}
		
	}
	
	@GetMapping("/comment/modify")
	public void modify(@RequestParam(name="commentNo") Long id, 
			@RequestParam(name="body") String updateBody,
			HttpServletResponse response) throws Exception {
	
//		Long boardId = commentService.findByComment(id).getBoardId();
		
		commentService.modify(id, updateBody);
		AlertMessage.alertAndBack(response, "댓글이 정상적으로 수정되었습니다.");
		
	}
	
	@GetMapping("/comment/delete")
	public void delete(@RequestParam(name="commentNo") Long id,
			HttpServletResponse response) throws Exception {
		
		commentService.delete(id);
		AlertMessage.alertAndBack(response, "댓글이 정상적으로 삭제되었습니다.");
	}
	
	@GetMapping("/api/comment/like")
	public String like(@RequestParam(name="commentNo") Long id,
			HttpServletResponse response) {
		commentService.updateLike(id);
		
		Long boardId = commentService.findByComment(id).getBoardId();
		
		return "redirect:/board?boardNo=" + boardId;
	}
}

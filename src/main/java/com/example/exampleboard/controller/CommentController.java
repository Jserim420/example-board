package com.example.exampleboard.controller;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	
	// 댓글 작성 로직
	@PostMapping("/api/comment/write")
	public void write(@CookieValue(name="userId", defaultValue = "") Long userId, 
			@RequestParam(name="boardNo") Long boardId,
			Board board, CommentForm commentForm,
			HttpServletResponse response) throws Exception  {
		Comment comment = new Comment();
		comment.setBody(commentForm.getBody());
		comment.setUserName(commentForm.getName());
		comment.setPassword(commentForm.getPassword());
//		System.out.println(comment.getPassword());
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
	
	// 댓글 수정
	@PostMapping("/api/comment/modify")
	public void modify(@RequestParam(name="commentNo") Long id, CommentForm commentForm,
			HttpServletResponse response) throws Exception {
	
//		Long boardId = commentService.findByComment(id).getBoardId();
		
		commentService.modify(id, commentForm.getBody());
		AlertMessage.alertAndClose(response, "댓글이 정상적으로 수정되었습니다.");
		
	}
	
	// 댓글 삭제
	@PostMapping("/api/comment/delete")
	public void delete(@RequestParam(name="commentNo") Long id,
			HttpServletResponse response) throws Exception {
		
		commentService.delete(id);
		AlertMessage.alertAndClose(response, "댓글이 정상적으로 삭제되었습니다.");
	}
	
	// 댓글 좋아요
//	@GetMapping("/api/comment/like")
//	public String like(@RequestParam(name="commentNo") Long id,
//			HttpServletResponse response) {
//		commentService.updateLike(id);
//		
//		Long boardId = commentService.findByComment(id).getBoardId();
//		
//		return "redirect:/board?boardNo=" + boardId;
//	}
	
	@GetMapping("/comment/userConfirm")
	public String confirm(@RequestParam(name="commentNo") Long id, Model model) {
	
		model.addAttribute("commentNo", id);
		model.addAttribute("selected", "modify");
		return "comment/userConfirm";
	}
	
	@GetMapping("/comment/delete")
	public String confirmDelete(@RequestParam(name="commentNo") Long id, Model model) {
	
		model.addAttribute("commentNo", id);
		model.addAttribute("selected", "delete");
		return "comment/userConfirm";
	}
	
	@PostMapping("/api/comment/userConfirm")
	public String confirmAPI(@RequestParam(name="commentNo") Long id, Comment comment, CommentForm commentForm,
			HttpServletResponse response, Model model) throws Exception {
		comment.setPassword(commentForm.getPassword());
		if(commentService.checkPassword(id, comment.getPassword())) {
			System.out.println(commentService.findByComment(id).getBody());
			model.addAttribute("comment", commentService.findByComment(id));
			return "comment/commentForm";
		}
		else AlertMessage.alertAndBack(response, "비밀번호가 일치하지 않습니다.");
		return "";
	}
	
}

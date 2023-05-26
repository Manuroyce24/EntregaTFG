package org.thirty.app.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.thirty.app.model.BlogUser;
import org.thirty.app.model.Comment;
import org.thirty.app.model.Post;
import org.thirty.app.service.BlogUserService;
import org.thirty.app.service.CommentService;
import org.thirty.app.service.PostService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
@SessionAttributes("comment")
public class CommentController {

	private final PostService postService;
	private final BlogUserService blogUserService;
	private final CommentService commentService;

	public CommentController(PostService postService, BlogUserService blogUserService, CommentService commentService) {
		this.postService = postService;
		this.blogUserService = blogUserService;
		this.commentService = commentService;
	}

	// El método GET que muestra el formulario de agregar comentarios
	@Secured("ROLE_USER")
	@GetMapping("/comment/{id}")
	public String showComment(@PathVariable Long id, Model model, Principal principal) {

		String authUsername = "anonymousUser";
		if (principal != null) {
			authUsername = principal.getName();
		}

		Optional<BlogUser> optionalBlogUser = this.blogUserService.findByUsername(authUsername);
		Optional<Post> postOptional = this.postService.getById(id);

		if (postOptional.isPresent() && optionalBlogUser.isPresent()) {
			Comment comment = new Comment();
			comment.setPost(postOptional.get());
			comment.setUser(optionalBlogUser.get());

			model.addAttribute("comment", comment);
			System.err.println("GET comentario/{id}: " + comment + "/" + id);
			return "commentForm";
		} else {
			System.err.println(
					"No se ha podido encontrar post por el id : " + id + " o por usuario loggueado : " + authUsername);
			return "error";
		}
	}

	// El método POST que valida y guarda el comentario
	@Secured("ROLE_USER")
	@PostMapping("/comment")
	public String validateComment(@Valid @ModelAttribute Comment comment, BindingResult bindingResult,
			SessionStatus sessionStatus) {
		System.err.println("POST comentario: " + comment);
		if (bindingResult.hasErrors()) {
			System.err.println("Comentario no validado");
			return "commentForm";
		} else {
			this.commentService.save(comment);
			System.err.println("SAVE comentario: " + comment);
			sessionStatus.setComplete();
			return "redirect:/post/" + comment.getPost().getId();
		}
	}

}

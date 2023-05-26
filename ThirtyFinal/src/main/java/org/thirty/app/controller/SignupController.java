package org.thirty.app.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.thirty.app.model.BlogUser;
import org.thirty.app.service.BlogUserService;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;

@Controller
@SessionAttributes("blogUser")
public class SignupController {

	private final BlogUserService blogUserService;

	public SignupController(BlogUserService blogUserService) {
		this.blogUserService = blogUserService;
	}

	// Acceso al registro
	@GetMapping("/signup")
	public String getRegisterForm(Model model) {
		BlogUser blogUser = new BlogUser();
		model.addAttribute("blogUser", blogUser);
		return "registerForm";
	}

	// Accion de Registro
	@PostMapping("/register")
	public String registerNewUser(@Valid @ModelAttribute BlogUser blogUser, BindingResult bindingResult, Model model,
			@RequestParam("confirmPassword") String confirmPassword, SessionStatus sessionStatus)
			throws RoleNotFoundException {
		System.err.println("newUser: " + blogUser);

		if (blogUserService.findByUsername(blogUser.getUsername()).isPresent()) {
			bindingResult.rejectValue("username", "error.username", "Username ya está registrado");
			System.err.println("Username con mensaje de error");
		}

		if (blogUserService.findByEmail(blogUser.getEmail()).isPresent()) {
			bindingResult.rejectValue("email", "error.email", "Correo electrónico ya está registrado");
			System.err.println("Correo electrónico con mensaje de error");
		}

		if (bindingResult.hasErrors()) {
			System.err.println("Nuevo usuario sin validar");
			return "registerForm";
		}

		if (!blogUser.getPassword().equals(blogUser.getConfirmPassword())) {
			bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Las contraseñas no coinciden");
			System.err.println("Confirmar Contraseña con mensaje de error");
			model.addAttribute("blogUser", blogUser);
			return "registerForm";
		}

		blogUser = this.blogUserService.saveNewBlogUser(blogUser);

		Authentication auth = new UsernamePasswordAuthenticationToken(blogUser, blogUser.getPassword(),
				blogUser.getAuthorities());
		System.err.println("AuthToken: " + auth);
		SecurityContextHolder.getContext().setAuthentication(auth);
		System.err.println(
				"SecurityContext Principal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		sessionStatus.setComplete();
		return "redirect:/home";
	}
}
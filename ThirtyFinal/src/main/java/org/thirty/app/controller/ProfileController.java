package org.thirty.app.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thirty.app.model.BlogUser;
import org.thirty.app.repository.BlogUserRepository;
import org.thirty.app.service.BlogUserService;

@Controller
@Transactional
public class ProfileController {

	@Autowired
	private BlogUserRepository blogUserRepository;

	@Autowired
	private BlogUserService blogUserService;

	// Muestra el perfil del usuario
	@GetMapping("/profile")
	public String showProfile(Authentication authentication, Model model) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		BlogUser blogUser = blogUserRepository.findByUsername(userDetails.getUsername()).orElse(null);

		if (blogUser != null) {
			model.addAttribute("profileUser", blogUser);
			return "profile";
		} else {
			return "redirect:/error";
		}
	}

	@GetMapping("/profile/{id}")
	public String showProfileById(@PathVariable Long id, Model model) {
		Optional<BlogUser> optionalBlogUser = blogUserRepository.findById(id);

		if (optionalBlogUser.isPresent()) {
			BlogUser blogUser = optionalBlogUser.get();
			model.addAttribute("profileUser", blogUser);
			return "profile";
		} else {
			model.addAttribute("error", "El perfil solicitado no existe.");
			return "error";
		}
	}

	// Acceso a editar el perfil
	@GetMapping("/editProfile")
	public String showEditProfileForm(Authentication authentication, Model model) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		BlogUser blogUser = blogUserRepository.findByUsername(userDetails.getUsername()).orElse(null);

		if (blogUser != null) {
			model.addAttribute("blogUser", blogUser);
			model.addAttribute("bindingResult", new BeanPropertyBindingResult(blogUser, "blogUser"));
			return "editProfile";
		} else {
			return "redirect:/error";
		}
	}

	// Editar perfil
	@PostMapping("/editProfile")
	public String processEditProfileForm(@Valid @ModelAttribute BlogUser blogUser, BindingResult bindingResult,
			Model model, Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
		if (bindingResult.hasErrors()) {
			return "editProfile";
		}

		try {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			BlogUser existingBlogUser = blogUserRepository.findByUsername(userDetails.getUsername()).orElse(null);

			if (existingBlogUser != null) {
				String currentPassword = request.getParameter("currentPassword");
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
				if (!passwordEncoder.matches(currentPassword, existingBlogUser.getPassword())) {
					model.addAttribute("error", "La contraseña actual es incorrecta.");
					return "editProfile";
				}

				existingBlogUser.setUsername(blogUser.getUsername());
				existingBlogUser.setApellido(blogUser.getApellido());
				existingBlogUser.setEmail(blogUser.getEmail());

				BlogUser savedBlogUser = blogUserRepository.save(existingBlogUser);

				model.addAttribute("message", "Perfil actualizado exitosamente.");
				model.addAttribute("profileUser", savedBlogUser);

				new SecurityContextLogoutHandler().logout(request, response, authentication);

				return "redirect:/logout";
			} else {
				model.addAttribute("error", "El usuario no existe.");
				return "editProfile";
			}

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Error al guardar los cambios en el perfil.");
			return "editProfile";
		}
	}

	@GetMapping("/users/search/{username}")
	public List<BlogUser> searchUsers(@PathVariable("username") String username) {
		return blogUserService.searchByUsername(username);
	}

	@GetMapping("/users/search")
	public ResponseEntity<List<String>> searchUsersByUsername(@RequestParam String username) {
		List<BlogUser> users = blogUserService.searchByUsername(username);
		List<String> usernames = users.stream().map(BlogUser::getUsername).collect(Collectors.toList());
		return ResponseEntity.ok(usernames);
	}

}

package org.thirty.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.thirty.app.model.BlogUser;
import org.thirty.app.repository.BlogUserRepository;

@Controller
public class MessagesController {

	@Autowired
	private BlogUserRepository blogUserRepository;

	// En construccion
	@GetMapping("/mensajes")
	public String getMessagesPage(Model model, Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		BlogUser currentUser = blogUserRepository.findByUsername(userDetails.getUsername()).orElse(null);
		model.addAttribute("currentUser", currentUser);
		return "mensajes";
	}
}

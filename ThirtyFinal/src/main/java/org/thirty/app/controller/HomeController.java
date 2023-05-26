package org.thirty.app.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thirty.app.model.BlogUser;
import org.thirty.app.model.FriendRequest;
import org.thirty.app.service.BlogUserService;
import org.thirty.app.service.FriendRequestService;
import org.thirty.app.service.PostService;

@Controller
public class HomeController {

	@Autowired
	private PostService postService;

	@Autowired
	private BlogUserService userService;

	@Autowired
	private FriendRequestService friendRequestService;

	// Carga la pagina principal y muestra los post.
	@GetMapping("/home")
	public String home(Model model, Principal principal, @RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "search", defaultValue = "") String searchTerm) {

		Pageable pageable = PageRequest.of(page, 5, Sort.by("creationDate").descending());

		if (searchTerm.isEmpty()) {
			model.addAttribute("posts", postService.findAllPosts(pageable));
		} else {
			model.addAttribute("posts", postService.searchPosts(searchTerm, pageable));
			model.addAttribute("search", searchTerm);
		}

		if (principal != null) {
			BlogUser currentUser = userService.findByUsername(principal.getName()).orElse(null);
			model.addAttribute("currentUser", currentUser);
			model.addAttribute("isAuthenticated", true);

			if (currentUser != null) {
				List<FriendRequest> friendRequests = friendRequestService.findPendingFriendRequests(currentUser);
				model.addAttribute("pendingFriendRequests", friendRequests);

				List<BlogUser> friends = userService.findFriends(currentUser);
				model.addAttribute("friends", friends);
			}
		} else {
			model.addAttribute("isAuthenticated", false);
		}

		return "home";
	}
}

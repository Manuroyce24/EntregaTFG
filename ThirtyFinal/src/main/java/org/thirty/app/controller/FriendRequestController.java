package org.thirty.app.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thirty.app.model.BlogUser;
import org.thirty.app.model.FriendRequest;
import org.thirty.app.service.BlogUserService;
import org.thirty.app.service.FriendRequestService;

@Controller
@RequestMapping("/friend-request")
public class FriendRequestController {

	@Autowired
	private FriendRequestService friendRequestService;

	@Autowired
	private BlogUserService blogUserService;

	// Obtener las solicitudes de amistad enviadas por el usuario actual y
	// mostrarlas en la vista
	@GetMapping("/sent")
	public String getSentFriendRequests(Model model, Principal principal) {
		BlogUser currentUser = blogUserService.findByUsername(principal.getName()).orElse(null);
		if (currentUser == null) {
			return "redirect:/error";
		}

		List<FriendRequest> sentFriendRequests = friendRequestService.findSentFriendRequests(currentUser);
		model.addAttribute("sentFriendRequests", sentFriendRequests);

		return "sentFriendRequests";
	}

	// Enviar una solicitud de amistad desde el remitente al receptor
	@PostMapping("/send")
	public String sendFriendRequest(@RequestParam("senderId") Long senderId,
			@RequestParam("receiverUsername") String receiverUsername, RedirectAttributes redirectAttributes) {
		BlogUser sender = blogUserService.findUserById(senderId);
		Optional<BlogUser> receiverOptional = blogUserService.findByUsername(receiverUsername);

		if (sender != null && receiverOptional.isPresent()) {
			BlogUser receiver = receiverOptional.get();

			if (sender.equals(receiver)) {
				redirectAttributes.addFlashAttribute("sameUserError",
						"No puedes enviarte una solicitud de amistad a ti mismo.");
				return "redirect:/friend-request/friends";
			}

			boolean alreadySent = friendRequestService
					.findBySenderAndReceiverAndStatus(sender, receiver, FriendRequest.FriendRequestStatus.PENDING)
					.isPresent();
			boolean alreadyReceived = friendRequestService
					.findBySenderAndReceiverAndStatus(receiver, sender, FriendRequest.FriendRequestStatus.PENDING)
					.isPresent();

			if (blogUserService.areFriends(sender, receiver)) {
				redirectAttributes.addFlashAttribute("alreadyFriendsError",
						"Ese usuario ya está en tu lista de amigos.");
				return "redirect:/friend-request/friends";
			} else if (alreadySent) {
				redirectAttributes.addFlashAttribute("alreadySentError",
						"Ya has enviado una solicitud de amistad a este usuario.");
				return "redirect:/friend-request/friends";
			} else if (alreadyReceived) {
				redirectAttributes.addFlashAttribute("alreadyReceivedError",
						"Ya tienes una solicitud pendiente de este usuario.");
				return "redirect:/friend-request/friends";
			} else if (!alreadySent && !alreadyReceived) {
				friendRequestService.sendFriendRequest(sender, receiver);
				redirectAttributes.addFlashAttribute("message", "La solicitud de amistad se ha enviado correctamente.");
				return "redirect:/friend-request/friends";
			}
		} else {
			redirectAttributes.addFlashAttribute("userNotFoundError", "El usuario no existe");
			return "redirect:/friend-request/friends";
		}

		redirectAttributes.addFlashAttribute("errorMessage", "Error al enviar la solicitud de amistad.");
		return "redirect:/friend-request/friends";
	}

	// Cancelar una solicitud de amistad con el ID proporcionado
	@PostMapping("/cancel")
	public String cancelFriendRequest(@RequestParam("friendRequestId") Long friendRequestId,
			RedirectAttributes redirectAttributes) {
		FriendRequest friendRequest = friendRequestService.findFriendRequestById(friendRequestId);

		if (friendRequest != null) {
			friendRequestService.cancelFriendRequest(friendRequest);
			redirectAttributes.addFlashAttribute("message", "La solicitud de amistad ha sido cancelada.");
			return "redirect:/friend-request/friends";
		}

		redirectAttributes.addFlashAttribute("errorMessage", "Error al cancelar la solicitud de amistad.");
		return "redirect:/error";
	}

	// Aceptar una solicitud de amistad con el ID proporcionado
	@PostMapping("/accept")
	public String acceptFriendRequest(@RequestParam("friendRequestId") Long friendRequestId) {
		FriendRequest friendRequest = friendRequestService.findFriendRequestById(friendRequestId);

		if (friendRequest != null) {
			friendRequestService.acceptFriendRequest(friendRequest);
			return "redirect:/friend-request/friends";
		}

		return "redirect:/error";
	}

	// Rechazar una solicitud de amistad con el ID proporcionado
	@PostMapping("/reject")
	public String rejectFriendRequest(@RequestParam("friendRequestId") Long friendRequestId) {
		FriendRequest friendRequest = friendRequestService.findFriendRequestById(friendRequestId);

		if (friendRequest != null) {
			friendRequestService.rejectFriendRequest(friendRequest);
			return "redirect:/friend-request/friends";
		}

		return "redirect:/error";
	}

	// Mostrar la lista de amigos del usuario actual en la vista
	@GetMapping("/friends")
	public String showFriends(Model model, Principal principal) {
		if (principal == null) {
			return "redirect:/login";
		}

		BlogUser currentUser = blogUserService.findByUsername(principal.getName()).orElse(null);
		if (currentUser == null) {
			return "redirect:/error";
		}

		List<BlogUser> friends = blogUserService.findFriends(currentUser);
		model.addAttribute("friends", friends);

		List<FriendRequest> pendingFriendRequests = friendRequestService.findPendingFriendRequests(currentUser);
		model.addAttribute("pendingFriendRequests", pendingFriendRequests);

		List<FriendRequest> sentFriendRequests = friendRequestService.findSentFriendRequests(currentUser);
		model.addAttribute("sentFriendRequests", sentFriendRequests);

		model.addAttribute("isAuthenticated", principal != null);

		model.addAttribute("currentUser", currentUser);

		return "friends";
	}

	// Eliminar a un amigo con el ID proporcionado
	@PostMapping("/delete")
	public String deleteFriend(@RequestParam("friendId") Long friendId, Principal principal,
			RedirectAttributes redirectAttributes) {

		String authUsername = "anonymousUser";
		if (principal != null) {
			authUsername = principal.getName();
		}

		BlogUser currentUser = blogUserService.findByUsername(authUsername).orElse(null);
		if (currentUser != null) {
			BlogUser friend = blogUserService.findUserById(friendId);
			if (friend != null) {
				blogUserService.deleteFriend(currentUser, friend);
				redirectAttributes.addFlashAttribute("message", "Amigo eliminado con éxito.");
				return "redirect:/friend-request/friends";
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "No se pudo encontrar al amigo para eliminar.");
				return "redirect:/error";
			}
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "No se pudo encontrar al usuario actual.");
			return "redirect:/error";
		}
	}

	// Obtener las solicitudes de amistad pendientes del usuario actual y mostrarlas
	// en la vista
	@GetMapping("/pending")
	public String getPendingFriendRequests(Model model, Principal principal) {
		BlogUser currentUser = blogUserService.findByUsername(principal.getName()).orElse(null);
		if (currentUser == null) {
			return "redirect:/error";
		}

		List<FriendRequest> pendingFriendRequests = friendRequestService.findPendingFriendRequests(currentUser);
		model.addAttribute("pendingFriendRequests", pendingFriendRequests);

		return "pendingFriendRequests";
	}

	// Buscar una solicitud de amistad según el término de búsqueda proporcionado
	@GetMapping("/search")
	public String searchFriendRequest(Model model, Principal principal,
			@RequestParam(name = "search", required = false) String searchTerm) {
		BlogUser currentUser = blogUserService.findByUsername(principal.getName()).orElse(null);
		if (currentUser == null) {
			return "redirect:/error";
		}

		if (searchTerm != null) {
			Optional<BlogUser> searchedUserOptional = blogUserService.findByUsername(searchTerm);
			if (searchedUserOptional.isPresent()) {
				BlogUser searchedUser = searchedUserOptional.get();
				boolean areFriends = blogUserService.areFriends(currentUser, searchedUser);
				model.addAttribute("searchedUser", searchedUser);
				model.addAttribute("areFriends", areFriends);
				return "friendRequestSearchResult";
			}
		}

		return "friendRequestSearch";
	}
}

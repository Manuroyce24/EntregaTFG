package org.thirty.app.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thirty.app.model.BlogUser;
import org.thirty.app.model.Post;
import org.thirty.app.service.BlogUserService;
import org.thirty.app.service.LikeService;
import org.thirty.app.service.PostService;

import net.coobird.thumbnailator.Thumbnails;

import org.springframework.http.MediaType;

import javax.imageio.ImageIO;
import javax.validation.Valid;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Controller
@SessionAttributes("post")
public class PostController {

	private final PostService postService;
	private final BlogUserService blogUserService;
	private final LikeService likeService;

	public PostController(PostService postService, BlogUserService blogUserService, LikeService likeService) {
		this.postService = postService;
		this.blogUserService = blogUserService;
		this.likeService = likeService;
	}

	// Método para obtener la imagen de un post
	@GetMapping("/post/image/{postId}")
	@ResponseBody
	public ResponseEntity<byte[]> getImageForPost(@PathVariable Long postId) {
		Optional<Post> optionalPost = postService.getById(postId);
		if (optionalPost.isPresent()) {
			Post post = optionalPost.get();
			byte[] imageBytes = post.getImage();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.valueOf(MediaType.IMAGE_JPEG_VALUE));
			return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// Obtiene el post por ID.
	@GetMapping("/post/{id}")
	public String getPost(@PathVariable Long id, Model model, Principal principal) {

		String authUsername = "anonymousUser";
		BlogUser currentUser = null;
		if (principal != null) {
			authUsername = principal.getName();
			Optional<BlogUser> optionalBlogUser = this.blogUserService.findByUsername(authUsername);
			if (optionalBlogUser.isPresent()) {
				currentUser = optionalBlogUser.get();
				model.addAttribute("currentUser", currentUser);
			}
		}

		Optional<Post> optionalPost = this.postService.getById(id);
		if (optionalPost.isPresent()) {
			Post post = optionalPost.get();
			model.addAttribute("post", post);
			if (currentUser != null && authUsername.equals(post.getUser().getUsername())) {
				model.addAttribute("isOwner", true);
			}
			return "post";
		} else {
			return "404";
		}
	}

	// Subida de imagen
	@PostMapping("/uploadImage")
	public String uploadImage(@RequestParam("image") MultipartFile file, @RequestParam("postId") Long postId,
			RedirectAttributes redirectAttributes) {
		try {
			if (file.isEmpty()) {
				redirectAttributes.addFlashAttribute("message", "Selecciona imagen para subir.");
				return "redirect:/post/" + postId;
			}

			String contentType = file.getContentType();
			if (contentType == null || !contentType.startsWith("image/")) {
				redirectAttributes.addFlashAttribute("message", "No es una imagen lo que estas intentando subir.");
				return "redirect:/post/" + postId;
			}

			long minSize = 1024; // 1 KB
			long maxSize = 10048576; // 10 MB

			long imageSize = file.getSize();
			if (imageSize < minSize || imageSize > maxSize) {
				redirectAttributes.addFlashAttribute("message", "La imagen debe tener un tamaño entre 1 KB y 10 MB.");
				return "redirect:/post/" + postId;
			}

			int maxWidth = 700;
			int maxHeight = 500;

			BufferedImage originalImage = ImageIO.read(file.getInputStream());

			if (originalImage == null) {
				redirectAttributes.addFlashAttribute("message",
						"No se pudo leer la imagen. Asegúrate de que el archivo es una imagen válida.");
				return "redirect:/post/" + postId;
			}

			int width = originalImage.getWidth();
			int height = originalImage.getHeight();

			if (width > maxWidth || height > maxHeight) {
				BufferedImage resizedImage = Thumbnails.of(originalImage).size(maxWidth, maxHeight).asBufferedImage();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(resizedImage, "jpg", baos);
				byte[] resizedImageBytes = baos.toByteArray();
				Optional<Post> optionalPost = postService.getById(postId);
				if (!optionalPost.isPresent()) {
					redirectAttributes.addFlashAttribute("message", "El post no se ha encontrado.");
					return "redirect:/post/" + postId;
				}
				Post post = optionalPost.get();
				post.setImage(resizedImageBytes);
				postService.save(post);
			} else {
				Optional<Post> optionalPost = postService.getById(postId);
				if (!optionalPost.isPresent()) {
					redirectAttributes.addFlashAttribute("message", "El post no se ha encontrado.");
					return "redirect:/post/" + postId;
				}
				Post post = optionalPost.get();
				post.setImage(file.getBytes());
				postService.save(post);
			}

			redirectAttributes.addFlashAttribute("message", "La imagen se ha subido correctamente");
		} catch (IOException e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "La subida de la imagen ha fallado");
		}

		return "redirect:/post/" + postId;
	}

	// Crea un nuevo Post
	@Secured("ROLE_USER")
	@GetMapping("/createNewPost")
	public String createNewPost(Model model, Principal principal) {

		String authUsername = "anonymousUser";
		BlogUser currentUser = null;
		if (principal != null) {
			authUsername = principal.getName();
			Optional<BlogUser> optionalBlogUser = this.blogUserService.findByUsername(authUsername);
			if (optionalBlogUser.isPresent()) {
				currentUser = optionalBlogUser.get();
				model.addAttribute("currentUser", currentUser);
			}
		}

		Optional<BlogUser> optionalBlogUser = this.blogUserService.findByUsername(authUsername);
		if (optionalBlogUser.isPresent()) {
			Post post = new Post();
			post.setUser(optionalBlogUser.get());
			model.addAttribute("post", post);
			return "postForm";
		} else {
			return "error";
		}
	}

	// Crea un nuevo POST, para otro constructor.
	@Secured("ROLE_USER")
	@PostMapping("/createNewPost")
	public String createNewPost(@Valid @ModelAttribute Post post, BindingResult bindingResult,
			SessionStatus sessionStatus) {
		System.err.println("POST post: " + post);
		if (bindingResult.hasErrors()) {
			System.err.println("Post no validado");
			return "postForm";
		}
		this.postService.save(post);
		System.err.println("SAVE post: " + post);
		sessionStatus.setComplete();
		return "redirect:/post/" + post.getId();
	}

	// Permite editar el POST.
	@Secured("ROLE_USER")
	@GetMapping("editPost/{id}")
	public String editPost(@PathVariable Long id, Model model, Principal principal) {

		String authUsername = "anonymousUser";
		BlogUser currentUser = null;
		if (principal != null) {
			authUsername = principal.getName();
			Optional<BlogUser> optionalBlogUser = this.blogUserService.findByUsername(authUsername);
			if (optionalBlogUser.isPresent()) {
				currentUser = optionalBlogUser.get();
				model.addAttribute("currentUser", currentUser);
			}
		}

		Optional<Post> optionalPost = this.postService.getById(id);
		if (optionalPost.isPresent()) {
			Post post = optionalPost.get();
			if (authUsername.equals(post.getUser().getUsername())) {
				model.addAttribute("post", post);
				System.err.println("EDIT post: " + post);
				return "postForm";
			} else {
				System.err.println("Este usuario no tiene permisos para la edicion: " + id);
				return "403";
			}
		} else {
			System.err.println("No se ha podido encontrar un post con esta ID: " + id);
			return "error";
		}
	}

	// Borra el post por ID
	@Secured("ROLE_USER")
	@GetMapping("/deletePost/{id}")
	public String deletePost(@PathVariable Long id, Model model, Principal principal) {

		String authUsername = "anonymousUser";
		BlogUser currentUser = null;
		if (principal != null) {
			authUsername = principal.getName();
			Optional<BlogUser> optionalBlogUser = this.blogUserService.findByUsername(authUsername);
			if (optionalBlogUser.isPresent()) {
				currentUser = optionalBlogUser.get();
				model.addAttribute("currentUser", currentUser);
			}
		}

		Optional<Post> optionalPost = this.postService.getById(id);

		if (optionalPost.isPresent()) {
			Post post = optionalPost.get();
			if (authUsername.equals(post.getUser().getUsername())) {
				this.postService.delete(post);
				System.err.println("DELETED post: " + post);
				return "redirect:/";
			} else {
				System.err.println("El usuario actual no tiene permisos para editar nada en la publicación de: " + id);
				return "403";
			}
		} else {
			System.err.println("No se ha podido encontrar post por el id: " + id);
			return "error";
		}
	}

	// Permite dar Like a los Post
	@Secured("ROLE_USER")
	@PostMapping("/likePost/{postId}")
	public String likePost(@PathVariable Long postId, Model model, Principal principal,
			RedirectAttributes redirectAttributes) {

		String authUsername = "anonymousUser";
		BlogUser currentUser = null;
		if (principal != null) {
			authUsername = principal.getName();
			Optional<BlogUser> optionalBlogUser = this.blogUserService.findByUsername(authUsername);
			if (optionalBlogUser.isPresent()) {
				currentUser = optionalBlogUser.get();
				model.addAttribute("currentUser", currentUser);
			}
		}

		Optional<BlogUser> optionalBlogUser = blogUserService.findByUsername(authUsername);
		if (optionalBlogUser.isPresent()) {
			BlogUser user = optionalBlogUser.get();
			likeService.likePost(postId, user);
			redirectAttributes.addFlashAttribute("message", "Has dado 'me gusta' a la publicación");
		} else {
			redirectAttributes.addFlashAttribute("message", "No se pudo dar 'me gusta' a la publicación");
		}

		return "redirect:/post/" + postId;
	}

	// Permite quitar un Like al Post
	@Secured("ROLE_USER")
	@PostMapping("/unlikePost/{postId}")
	public String unlikePost(@PathVariable Long postId, Principal principal, RedirectAttributes redirectAttributes) {

		String authUsername = "anonymousUser";
		if (principal != null) {
			authUsername = principal.getName();
		}

		Optional<BlogUser> optionalBlogUser = blogUserService.findByUsername(authUsername);
		if (optionalBlogUser.isPresent()) {
			BlogUser user = optionalBlogUser.get();
			likeService.unlikePost(postId, user);
			redirectAttributes.addFlashAttribute("message", "Has retirado el 'me gusta' a la publicación");
		} else {
			redirectAttributes.addFlashAttribute("message", "No se pudo retirar el 'me gusta' a la publicación");
		}

		return "redirect:/post/" + postId;
	}

}

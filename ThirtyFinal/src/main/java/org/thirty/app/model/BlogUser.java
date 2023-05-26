package org.thirty.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Pattern;


@Getter
@Setter
@Entity
@Table(name = "users")
@SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq", initialValue = 10, allocationSize = 1)
public class BlogUser implements UserDetails {

	private static final int MIN_USERNAME_LENGTH = 3;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
	@Column(name = "id")
	private Long id;

	@Length(min = MIN_USERNAME_LENGTH, message = "El nombre de  usuario debe tener al menos " + MIN_USERNAME_LENGTH
			+ " caracteres")
	@NotEmpty(message = "Por favor, introduzca un nombre de usuario")
	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@Column(name = "email", nullable = true)
	@NotEmpty(message = "Por favor, introduzca un correo electrónico")
	private String email;

	@NotEmpty(message = "Por favor, introduzca tus Apellidos")
	private String Apellido;

	public String getApellido() {
		return Apellido;
	}

	public void setApellido(String apellido) {
		Apellido = apellido;
	}

	@JsonIgnore
	@Column(name = "password", nullable = false)
	@Pattern(regexp = "^(?=.*[A-Z]).{6,}$", message = "La contraseña debe tener al menos 6 caracteres y contener al menos una letra mayúscula")
	private String password;

	private String confirmPassword;

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<FriendRequest> sentFriendRequests = new ArrayList<>();

	@OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<FriendRequest> receivedFriendRequests = new ArrayList<>();

	@ManyToMany
	private List<BlogUser> friends = new ArrayList<>();

	@Column(name = "enabled", nullable = false)
	private Boolean enabled;

	@OneToMany(mappedBy = "user")
	private Collection<Post> posts;

	@ManyToMany(cascade = CascadeType.REMOVE)
	@JoinTable(name = "users_authorities", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "authority_id"))
	private Collection<Authority> authorities;

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "BlogUser{" + "id=" + id + ", username='" + username + '\'' + ", password='" + password + '\''
				+ ", authorities=" + authorities + '}';
	}

	public List<FriendRequest> getSentFriendRequests() {
		return sentFriendRequests;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Collection<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<Authority> authorities) {
		this.authorities = authorities;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSentFriendRequests(List<FriendRequest> sentFriendRequests) {
		this.sentFriendRequests = sentFriendRequests;
	}

	public List<FriendRequest> getReceivedFriendRequests() {
		return receivedFriendRequests;
	}

	public void setReceivedFriendRequests(List<FriendRequest> receivedFriendRequests) {
		this.receivedFriendRequests = receivedFriendRequests;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<BlogUser> getFriends() {
		return friends;
	}

	public Collection<Post> getPosts() {
		return posts;
	}
}
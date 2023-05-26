package org.thirty.app.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@Table(name = "comments")
@SequenceGenerator(name = "comment_seq_gen", sequenceName = "comment_seq", initialValue = 10, allocationSize = 1)
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq_gen")
	@Column(name = "id")
	private Long id;

	@Column(columnDefinition = "TEXT", nullable = false)
	@NotEmpty(message = "Comment body can not be empty! Write something sane for the love of Internet, would you?")
	private String body = "";

	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	@Column(name = "creation_date", nullable = false, updatable = false)
	private Date creationDate;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
	private Post post;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
	private BlogUser user;

	@Override
	public String toString() {
		return "Comment{" + "id=" + id + ", body='" + body + '\'' + ", creationDate=" + creationDate + ", post_id="
				+ post.getId() + ", username=" + user.getUsername() + '}';
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public BlogUser getUser() {
		return user;
	}

	public void setUser(BlogUser user) {
		this.user = user;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public long getPostId() {
		return id;
	}
}

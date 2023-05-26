package org.thirty.app.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "friend_requests")
@SequenceGenerator(name = "friend_request_seq_gen", sequenceName = "friend_request_seq", initialValue = 10, allocationSize = 1)
public class FriendRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friend_request_seq_gen")
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "sender_id", nullable = false)
	private BlogUser sender;

	@ManyToOne
	@JoinColumn(name = "receiver_id", nullable = false)
	private BlogUser receiver;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private FriendRequestStatus status;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public FriendRequest() {
		this.createdAt = LocalDateTime.now();
	}

	public enum FriendRequestStatus {
		PENDING, ACCEPTED, REJECTED
	}

	public void setSender(BlogUser sender) {
		this.sender = sender;
	}

	public void setReceiver(BlogUser receiver) {
		this.receiver = receiver;
	}

	public BlogUser getSender() {
		return this.sender;
	}

	public BlogUser getReceiver() {
		return this.receiver;
	}

	public FriendRequestStatus getStatus() {
		return status;
	}

	public void setStatus(FriendRequestStatus status) {
		this.status = status;
	}

	public Long getId() {
		return id;
	}
}

package org.thirty.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thirty.app.model.BlogUser;
import org.thirty.app.model.FriendRequest;
import org.thirty.app.model.FriendRequest.FriendRequestStatus;
import org.thirty.app.repository.FriendRequestRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FriendRequestService {

	@Autowired
	private FriendRequestRepository friendRequestRepository;

	@Autowired
	private BlogUserService blogUserService;

	public void sendFriendRequest(BlogUser sender, BlogUser receiver) {
		if (!blogUserService.areFriends(sender, receiver) && !alreadySent(sender, receiver)
				&& !alreadyReceived(sender, receiver)) {
			FriendRequest friendRequest = new FriendRequest();
			friendRequest.setSender(sender);
			friendRequest.setReceiver(receiver);
			friendRequest.setStatus(FriendRequestStatus.PENDING);
			friendRequestRepository.save(friendRequest);
		}
	}

	public boolean alreadySent(BlogUser sender, BlogUser receiver) {
		return friendRequestRepository.findBySenderAndReceiverAndStatus(sender, receiver, FriendRequestStatus.PENDING)
				.isPresent();
	}

	public boolean alreadyReceived(BlogUser sender, BlogUser receiver) {
		return friendRequestRepository.findBySenderAndReceiverAndStatus(receiver, sender, FriendRequestStatus.PENDING)
				.isPresent();
	}

	public List<FriendRequest> findPendingFriendRequests(BlogUser currentUser) {
		return friendRequestRepository.findByReceiverAndStatus(currentUser, FriendRequestStatus.PENDING);
	}

	public void rejectFriendRequest(FriendRequest friendRequest) {
		friendRequest.setStatus(FriendRequest.FriendRequestStatus.REJECTED);
		friendRequestRepository.save(friendRequest);
	}

	public FriendRequest findFriendRequestById(Long friendRequestId) {
		Optional<FriendRequest> friendRequestOptional = friendRequestRepository.findById(friendRequestId);
		return friendRequestOptional.orElse(null);
	}

	public void acceptFriendRequest(FriendRequest friendRequest) {
		friendRequest.setStatus(FriendRequest.FriendRequestStatus.ACCEPTED);
		BlogUser sender = friendRequest.getSender();
		BlogUser receiver = friendRequest.getReceiver();

		sender.getFriends().add(receiver);
		receiver.getFriends().add(sender);

		friendRequestRepository.save(friendRequest);
	}

	public Optional<FriendRequest> findBySenderAndReceiverAndStatus(BlogUser sender, BlogUser receiver,
			FriendRequestStatus status) {
		return friendRequestRepository.findBySenderAndReceiverAndStatus(sender, receiver, status);
	}

	public void cancelFriendRequest(FriendRequest friendRequest) {
		friendRequestRepository.delete(friendRequest);
	}

	public List<FriendRequest> findSentFriendRequests(BlogUser currentUser) {
		return friendRequestRepository.findBySenderAndStatus(currentUser, FriendRequest.FriendRequestStatus.PENDING);
	}

}

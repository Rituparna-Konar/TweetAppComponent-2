package com.iiht.tweetapp.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.iiht.tweetapp.exception.TweetNotFoundException;
import com.iiht.tweetapp.model.TweetDetails;

@Service
public interface TweetServices {
	ResponseEntity<Object> getAllTweets();
	ResponseEntity<Object> getTweetsByUser(String username);
	ResponseEntity<Object> addTweet(String username,TweetDetails tweetDetails);
	ResponseEntity<Object> updateTweet(Long id,TweetDetails tweetDetails) throws TweetNotFoundException;
	ResponseEntity<Object> deleteTweet(Long id) throws TweetNotFoundException;
	ResponseEntity<Object> likeTweet(String username,Long id) throws TweetNotFoundException;
	ResponseEntity<Object> replyTweet(String username,Long id,String reply) throws TweetNotFoundException;
	ResponseEntity<Object> unLikeTweet(String username,Long id) throws TweetNotFoundException;
	
}

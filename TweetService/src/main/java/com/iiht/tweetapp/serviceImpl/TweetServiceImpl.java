package com.iiht.tweetapp.serviceImpl;

import java.time.*;
import java.util.*;
import java.util.stream.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.iiht.tweetapp.config.AppConfigs;
import com.iiht.tweetapp.exception.TweetNotFoundException;
import com.iiht.tweetapp.model.ResponseTweet;
import com.iiht.tweetapp.model.TweetDetails;
import com.iiht.tweetapp.model.UserData;
import com.iiht.tweetapp.repository.TweetRepository;
import com.iiht.tweetapp.repository.UserRepository;
import com.iiht.tweetapp.service.TweetServices;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TweetServiceImpl implements TweetServices{
	@Autowired
	private TweetRepository tweetRepository;
	@Autowired
	private UserRepository userRepository;
	private List<ResponseTweet> formatTweetList(List<TweetDetails> list) {
		log.info("inside format tweet list Implementation to format data");
		List<ResponseTweet> result=new ArrayList<>();
		for(TweetDetails tweet:list) {
			Optional<UserData> user=userRepository.findById(tweet.getUsername());
			List<String> likes = new ArrayList<>();
			List<String> replies = new ArrayList<>();
			if(tweet.getLikes()!=null && !tweet.getLikes().isEmpty())
				likes = Arrays.asList(tweet.getLikes().split(","));
			if(tweet.getReplies()!=null && !tweet.getReplies().isEmpty())
				replies = Arrays.asList(tweet.getReplies().split(","));
			ResponseTweet tweet1=new ResponseTweet(tweet.getId(),tweet.getHandleName(),tweet.getMessage(),
					tweet.getTime(),tweet.getUsername(),likes,replies,user.get().getFirstName(),
					user.get().getLastName(),tweet.isStatus());
			result.add(tweet1);
		}
		return result;
	}

	@Override
	public ResponseEntity<Object> getAllTweets() {
		log.info("inside tweet service Implementation to get all tweets");
		List<TweetDetails> tweets=tweetRepository.findAll().stream().filter(o->o.isStatus())
				.sorted((o1,o2)-> o2.getTime().compareTo(o1.getTime()))
				.collect(Collectors.toList());
		//if(!tweets.isEmpty() && tweets.size()>0)
			return new ResponseEntity<>(formatTweetList(tweets),HttpStatus.OK);
		//return new ResponseEntity<Object>(new ResponseMessage("No Tweets Found"),HttpStatus.NO_CONTENT);
	}
	@Override
	public ResponseEntity<Object> getTweetsByUser(String username) {
		log.info("inside tweet service Implementation to get tweets by username");
		List<TweetDetails> tweets=tweetRepository.findAll().stream().filter(o->o.getUsername().equals(username)&&o.isStatus())
				.sorted((o1,o2)-> o2.getTime().compareTo(o1.getTime()))
				.collect(Collectors.toList());
		//if(!tweets.isEmpty()  && tweets.size()>0)
			return new ResponseEntity<>(formatTweetList(tweets),HttpStatus.OK);
		//return new ResponseEntity<Object>(new ResponseMessage("No Tweets Found"),HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Object> addTweet(String username,TweetDetails tweetDetails) {
		log.info("inside tweet service Implementation to add tweet");
			//tweetDetails.setId(Longs.timeBased());
			tweetDetails.setUsername(username);
			tweetDetails.setStatus(true);
			tweetDetails.setTime(LocalDateTime.now());
			tweetRepository.save(tweetDetails);
			return new ResponseEntity<>("Added Tweet Successfully",HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Object> updateTweet(Long id, TweetDetails tweetDetails) throws TweetNotFoundException {
		log.info("inside tweet service Implementation to update tweet data");
			Optional<TweetDetails> tweet=tweetRepository.findById(id);
			if(!tweet.isPresent()) {
				throw new TweetNotFoundException("Tweet not found exception");
			}
			tweet.get().setHandleName(tweetDetails.getHandleName());
			tweet.get().setMessage(tweetDetails.getMessage());
			tweetRepository.save(tweet.get());
			return new ResponseEntity<>("Updated Tweet Successfully",HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Object> deleteTweet(Long id) throws TweetNotFoundException {
		log.info("inside tweet service Implementation to delete tweet");
			Optional<TweetDetails> tweet=tweetRepository.findById(id);
			if(!tweet.isPresent()) {
				throw new TweetNotFoundException("Tweet not found exception");
			}
			tweet.get().setStatus(false);
			tweetRepository.save(tweet.get());
			return new ResponseEntity<>("Deleting Tweet Successfully",HttpStatus.OK);
		
	}

	@Override
	public ResponseEntity<Object> likeTweet(String username,Long id) throws TweetNotFoundException {
		log.info("inside tweet service Implementation to like tweet");
			Optional<TweetDetails> tweet=tweetRepository.findById(id);
			if(!tweet.isPresent()) {
				throw new TweetNotFoundException("Tweet not found exception");
			}
			if(tweet.get().getLikes()!=null && !tweet.get().getLikes().isEmpty()) {
				tweet.get().setLikes(tweet.get().getLikes()+","+username);
				tweet.get().setLikes(tweet.get().getLikes());
			}
			else {
//				String l="";
//				l.add(username);
				tweet.get().setLikes(username);
//				tweet.get().setLikes(l);
			}
			tweetRepository.save(tweet.get());
			return new ResponseEntity<>("liked the Tweet Successfully",HttpStatus.OK);
	}
	
	
	@Override
	public ResponseEntity<Object> unLikeTweet(String username,Long id) throws TweetNotFoundException {
		log.info("inside tweet service Implementation to unlike tweet");
			Optional<TweetDetails> tweet=tweetRepository.findById(id);
			if(!tweet.isPresent()) {
				throw new TweetNotFoundException("Tweet not found exception");
			}
			if(tweet.get().getLikes()!=null) {
				String changedLikes = tweet.get().getLikes().replace(","+username,"");
				tweet.get().setLikes(changedLikes.replace(username,""));
			}
			tweetRepository.save(tweet.get());
			return new ResponseEntity<Object>("Un-liked the Tweet Successfully",HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Object> replyTweet(String username,Long id, String reply) throws TweetNotFoundException {
		log.info("inside tweet service Implementation to reply tweet");
			Optional<TweetDetails> tweet=tweetRepository.findById(id);
			if(!tweet.isPresent()) {
				throw new TweetNotFoundException("Tweet not found exception");
			}
			Optional<UserData> user=userRepository.findById(username);
			reply=reply.replace("+", " ");
			reply=reply.replace("=", "");
			reply=user.get().getFirstName()+" "+user.get().getLastName()+"-"+reply;
			if(tweet.get().getReplies()!=null)
				tweet.get().setReplies(tweet.get().getReplies()+" , "+reply);
			else {
				tweet.get().setReplies(reply);
			}
			System.out.println(reply);
			System.out.println(tweet.get());
			tweetRepository.save(tweet.get());
			return new ResponseEntity<Object>("Replied to Tweet Successfully",HttpStatus.OK);
	}
	@KafkaListener(topics = AppConfigs.topicName,groupId = "delete")
	public ResponseEntity<Object> deleteTweetKafka(Long id) throws TweetNotFoundException {
		log.info("inside tweet service Implementation to delete tweet");
		Optional<TweetDetails> tweet=tweetRepository.findById(id);
		if(!tweet.isPresent()) {
			throw new TweetNotFoundException("Tweet not found exception");
		}
		tweet.get().setStatus(false);
		tweetRepository.save(tweet.get());
		return new ResponseEntity<Object>("Deleting Tweet Successfully",HttpStatus.OK);
	}

}

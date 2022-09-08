package com.iiht.tweetapp.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;
import java.time.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tweet_details")
public class TweetDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column
	private String handleName;
	@Column 
	private String message;
	@Column
	private LocalDateTime time;
	@Column
	private String username;
	@Column
	private String likes;
	@Column
	private String replies;
	@Column
	@JsonIgnore
	private boolean Status;

}

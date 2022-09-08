package com.iiht.tweetapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="user_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserData {
		@Id
		private String userName;
	    @Column
	    private String firstName;
	    @Column
	    private String lastName;
	    @Column
	    private String password;
	    @Column
	    private long contactNo;
	    
}

package com.iiht.tweetapp.service;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.iiht.tweetapp.exception.UserExistsException;
import com.iiht.tweetapp.model.LoginDetails;
import com.iiht.tweetapp.model.ResponseMessage;
import com.iiht.tweetapp.model.UserData;
import com.iiht.tweetapp.repository.UserRepository;
import com.iiht.tweetapp.seviceimpl.CustomerDetailsService;
import com.iiht.tweetapp.seviceimpl.JwtUtil;
import com.iiht.tweetapp.seviceimpl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	
	@Mock
	private UserRepository userRepository;
	@Mock
	private CustomerDetailsService custdetailservice;
	@Mock
	private JwtUtil jwtutil;
	@InjectMocks
	private UserServiceImpl userService;
	
	UserDetails userdetails;
	UserData userData;
	Optional<UserData> user;
	
	@Test
	void loginTest() {
		userdetails=new User("shilpa", "shilpa", new ArrayList<>());
		when(custdetailservice.loadUserByUsername("shilpa")).thenReturn(userdetails);
		userData=new UserData("shilpa", "M", "shilpa", "shilpa", 0);
		when(userRepository.findById("shilpa")).thenReturn(Optional.of(userData));
		LoginDetails login=new LoginDetails("shilpa","shilpa");
		when(jwtutil.generateToken(userdetails)).thenReturn("token");
		assertEquals(200, userService.login(login).getStatusCodeValue());
	}
	@Test
	void loginTestFail() {
		userdetails=new User("shilpa", "shilpa", new ArrayList<>());
		when(custdetailservice.loadUserByUsername("shilpa")).thenReturn(userdetails);
		userData=new UserData("shilpa", "M", "shilpa", "shilpa", 0);
		when(userRepository.findById("shilpa")).thenReturn(Optional.of(userData));
		LoginDetails login=new LoginDetails("shilpa","shilpa1");
		assertEquals(403, userService.login(login).getStatusCodeValue());
	}
	
	@Test
	void registerTest() {
		userData=new UserData("shilpa", "M", "shilpa", "shilpa", 0);
		when(userRepository.findById("shilpa")).thenReturn(Optional.ofNullable(null));
		when(userRepository.save(userData)).thenReturn(userData);
		assertEquals(201, userService.register(userData).getStatusCodeValue());
	}
	@Test
	void registerTestFail() {
		userData=new UserData("shilpa", "M", "shilpa", "shilpa", 0);
		when(userRepository.findById("shilpa")).thenReturn(Optional.ofNullable(userData));
		assertThrows(UserExistsException.class,()-> userService.register(userData));
	}
	@Test
	void registerTestFail2() {
		userData=new UserData("shilpa", "M", "shilpa", "shilpa", 0);
		lenient().when(userRepository.findById("shilpa")).thenReturn(Optional.of(userData));
		lenient().when(userRepository.save(userData)).thenThrow(RuntimeException.class);
		assertThrows(UserExistsException.class,()-> userService.register(userData));
	}
	
	@Test
	void getAllUsers() {
		List<UserData> users=new ArrayList<>();
		users.add(new UserData());
		users.add(new UserData());
		when(userRepository.findAll()).thenReturn(users);
		assertEquals(users, userService.getAllUsers().getBody());
	}

	@Test
	void getUsersByUsername() {
		List<UserData> users=new ArrayList<>();
		users.add(new UserData("shilpa", "M", "shilpa", "shilpa", 0));
		users.add(new UserData("shilpa", "M", "shilpa", "shilpa", 0));
		when(userRepository.findAll()).thenReturn(users);
		assertEquals(users, ((ResponseMessage) userService.searchByUsername("shi").getBody()).getResponse());
	}
	
	@Test
	void forgotPassword() {
		userData=new UserData("shilpa", "M", "shilpa", "shilpa", 0);
		LoginDetails login=new LoginDetails("shilpa","shilpa1");
		when(userRepository.findById("shilpa")).thenReturn(Optional.ofNullable(userData));
		when(userRepository.save(userData)).thenReturn(userData);
		assertEquals(200, userService.forgotPassword(login).getStatusCodeValue());
	}
	@Test
	void forgotPasswordFail() {
		userData=new UserData("shilpa", "M", "shilpa", "shilpa", 0);
		LoginDetails login=new LoginDetails("shilpa","shilpa1");
		when(userRepository.findById("shilpa")).thenReturn(Optional.ofNullable(null));
		assertThrows(UsernameNotFoundException.class,()-> userService.forgotPassword(login));
	}
	@Test
	void validateToken() {
		userData=new UserData("shilpa", "M", "shilpa", "shilpa", 0);
		when(jwtutil.validateToken("token")).thenReturn(true);
		when(jwtutil.extractUsername("token")).thenReturn("shilpa");
		when(userRepository.findById("shilpa")).thenReturn(Optional.ofNullable(userData));
		assertEquals(200, userService.validate("Bearer token").getStatusCodeValue());
	}
	@Test
	void validateTokenFail() {
		userData=new UserData("shilpa", "M", "shilpa", "shilpa", 0);
		when(jwtutil.validateToken("token")).thenReturn(true);
		when(jwtutil.extractUsername("token")).thenReturn("shilpa");
		when(userRepository.findById("shilpa")).thenReturn(Optional.ofNullable(null));
		assertEquals(204, userService.validate("Bearer token").getStatusCodeValue());
	}
	@Test
	void validateTokenFail2() {
		when(jwtutil.validateToken("token")).thenReturn(false);
		assertEquals(204, userService.validate("Bearer token").getStatusCodeValue());
	}
}

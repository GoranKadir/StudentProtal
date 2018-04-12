package com.gorankadir.se.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gorankadir.se.exception.ResourceNotFoundException;
import com.gorankadir.se.model.User;
import com.gorankadir.se.payload.PagedResponse;
import com.gorankadir.se.payload.UserIdentityAvailability;
import com.gorankadir.se.repository.UserRepository;
import com.gorankadir.se.security.CurrentUser;
import com.gorankadir.se.security.UserPrincipal;
import com.gorankadir.se.util.AppConstants;

@RestController
@RequestMapping("/ap")
public class AdminController {
	
	 @Autowired
	    private UserRepository userRepository;
	 
	    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	    
	    @GetMapping("/user/checkUsernameAvailability")
	    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
	        Boolean isAvailable = !userRepository.existsByUsername(username);
	        return new UserIdentityAvailability(isAvailable);
	    }

	    @GetMapping("/user/checkEmailAvailability")
	    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
	        Boolean isAvailable = !userRepository.existsByEmail(email);
	        return new UserIdentityAvailability(isAvailable);
	    }
	    
	    @GetMapping("/users/all")
	    public List<User> getAllUsers() {
	        List<User> user = userRepository.findAll();
	        
	        return user;
	    }
}

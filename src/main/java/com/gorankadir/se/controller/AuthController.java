package com.gorankadir.se.controller;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gorankadir.se.exception.AppException;
import com.gorankadir.se.model.Role;
import com.gorankadir.se.model.RoleName;
import com.gorankadir.se.model.User;
import com.gorankadir.se.payload.ApiResponse;
import com.gorankadir.se.payload.JwtAuthenticationResponse;
import com.gorankadir.se.payload.LoginRequest;
import com.gorankadir.se.payload.SignUpRequest;
import com.gorankadir.se.repository.RoleRepository;
import com.gorankadir.se.repository.UserRepository;
import com.gorankadir.se.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
    AuthenticationManager authenticationManager;
	
	@Autowired
    JwtTokenProvider tokenProvider;
	
	@Autowired
    PasswordEncoder passwordEncoder;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getUsernameOrEmail(),
						loginRequest.getPassword()
				)
			);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
		
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){
		
		if(userRepository.existsByUsername(signUpRequest.getUsername())){
			return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
					HttpStatus.BAD_REQUEST);
		}
		if(userRepository.existsByEmail(signUpRequest.getPassword())){
			return new ResponseEntity(new ApiResponse(false, "email is already taken!"),
					HttpStatus.BAD_REQUEST);
		}
		
		User user = new User(signUpRequest.getName(),
				signUpRequest.getUsername(),
				signUpRequest.getEmail(),
				signUpRequest.getPassword()
		 );
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
				.orElseThrow(() -> new AppException("User role not set"));
		
		user.setRoles(Collections.singleton(userRole));
		
		User result = userRepository.save(user);
		
		 URI location = ServletUriComponentsBuilder
				 .fromCurrentContextPath().path("/users/{username}")
	             .buildAndExpand(result.getUsername()).toUri();
		 
		 return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
		
	}
		

}

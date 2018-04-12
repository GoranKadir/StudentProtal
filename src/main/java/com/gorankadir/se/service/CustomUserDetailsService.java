package com.gorankadir.se.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gorankadir.se.model.User;
import com.gorankadir.se.repository.UserRepository;
import com.gorankadir.se.security.UserPrincipal;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
		User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
				.orElseThrow(() ->
						new UsernameNotFoundException("Username not found username or email: " + usernameOrEmail));
		return UserPrincipal.create(user);
	}
	
	@Transactional
	public UserDetails loadUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(
				() -> new UsernameNotFoundException("User not found on id :" + id));
		return UserPrincipal.create(user);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}

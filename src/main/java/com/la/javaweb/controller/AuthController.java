package com.la.javaweb.controller;

import com.la.javaweb.dto.request.UserLogin;
import com.la.javaweb.dto.request.UserRegister;
import com.la.javaweb.dto.response.JwtResponse;
import com.la.javaweb.service.IUserService;
import com.la.javaweb.util.exception.AppException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private IUserService userService;
	
	@PostMapping("/login")
	public ResponseEntity<JwtResponse> handleLogin(@Valid @RequestBody UserLogin userLogin) throws AppException {
		return new ResponseEntity<>(userService.login(userLogin), HttpStatus.OK);
	}

	
	@PostMapping("/register")
	public ResponseEntity<String> handleRegister(@Valid @RequestBody UserRegister userRegister) throws AppException {
		userService.register(userRegister);
		return new ResponseEntity<>("Register successful",HttpStatus.CREATED);
	}
	
}

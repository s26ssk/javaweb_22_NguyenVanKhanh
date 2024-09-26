package com.la.javaweb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class JwtResponse {
	private String token;
	
	private Long expired;
	
	private final String type = "Bearer";
	
	private String fullName;
	
	private String username;
	
	private Set<String> roles;
	private boolean status;
}

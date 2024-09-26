package com.la.javaweb.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserRegister {
	@NotEmpty(message = "FullName cannot be empty")
	private String fullName;
	@NotEmpty(message = "Username cannot be empty")
	private String username;
	@NotEmpty(message = "Password cannot be empty")
	@Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()-+=]).*$",
			message = "Password must start with an uppercase letter, contain at least one digit, and one special character")
	private String password;
	private Set<String> roles;
}

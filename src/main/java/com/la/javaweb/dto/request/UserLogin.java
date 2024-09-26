package com.la.javaweb.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserLogin {
	@NotEmpty(message = "Username cannot be empty")
	private String username;
	@NotEmpty(message = "Password cannot be empty")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()-+=]).*$",
			message = "Password must start with an uppercase letter, contain at least one digit, and one special character")
	private String password;
}

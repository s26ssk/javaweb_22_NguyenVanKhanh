package com.la.javaweb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CheckoutRequest {
    private String note;
    private String fullAddress;
    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "(\\+84|0)\\d{9,10}", message = "Invalid phone number")
    private String phone;
    private String receiveName;
}

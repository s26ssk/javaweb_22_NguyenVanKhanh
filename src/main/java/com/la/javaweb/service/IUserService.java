package com.la.javaweb.service;


import com.la.javaweb.dto.request.AddressRequest;
import com.la.javaweb.dto.request.UserLogin;
import com.la.javaweb.dto.request.UserRegister;
import com.la.javaweb.dto.response.AddressResponse;
import com.la.javaweb.dto.response.JwtResponse;
import com.la.javaweb.model.Users;
import com.la.javaweb.util.exception.AppException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUserService {
	Optional<Users> getUserById(Long userId);
	Optional<Users> getUserByUsername(String username);
	
	void register(UserRegister userRegister) throws AppException;
	
	JwtResponse login(UserLogin userLogin) throws AppException;
	Page<Users> getAllUsers(Pageable pageable);
	Page<Users> searchUsersByUsername(String username, Pageable pageable);
	boolean toggleUserStatus(Long userId) throws AppException;
	void changePassword(String username, String newPassword) throws AppException;
	boolean checkPassword(String username, String password) throws AppException;
	void updateUser(Users user);
	void addAddress(String username, AddressRequest addressRequest);
	void deleteAddress(String username, Long addressId) throws AppException;
	List<AddressResponse> getUserAddresses(String username) throws AppException;
	AddressResponse getUserAddressById(String username, Long addressId) throws AppException;


}

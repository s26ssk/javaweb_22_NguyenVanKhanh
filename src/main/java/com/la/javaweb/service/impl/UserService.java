package com.la.javaweb.service.impl;

import com.la.javaweb.dto.request.AddressRequest;
import com.la.javaweb.dto.request.UserLogin;
import com.la.javaweb.dto.request.UserRegister;
import com.la.javaweb.dto.response.AddressResponse;
import com.la.javaweb.dto.response.JwtResponse;
import com.la.javaweb.model.Address;
import com.la.javaweb.model.RoleName;
import com.la.javaweb.model.Roles;
import com.la.javaweb.model.Users;
import com.la.javaweb.repository.IAddressRepository;
import com.la.javaweb.repository.IUserRepository;
import com.la.javaweb.security.jwt.JwtProvider;
import com.la.javaweb.security.user_principal.UserPrincipal;
import com.la.javaweb.service.IRoleService;
import com.la.javaweb.service.IUserService;
import com.la.javaweb.util.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
	
	@Value("${jwt.expired}")
	private Long EXPIRED;
	
	@Autowired
	private IUserRepository userRepository;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationProvider authenticationProvider;
	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private IAddressRepository addressRepository;

	@Override
	public Page<Users> getAllUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	@Override
	public Page<Users> searchUsersByUsername(String username, Pageable pageable) {
		return userRepository.findByUsernameContaining(username, pageable);
	}

	@Override
	public Optional<Users> getUserById(Long userId) {
		return userRepository.findByUserId(userId);
	}

	@Override
	public Optional<Users> getUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public void register(UserRegister userRegister) throws AppException{
		if (userRepository.existsByUsername(userRegister.getUsername())) {
			throw new AppException("Account already exists");
		}
		Set<Roles> roles = new HashSet<>();
		
		// Nếu không có quyền được truyền lên, mặc định là role user
		if (userRegister.getRoles() == null || userRegister.getRoles().isEmpty()) {
			roles.add(roleService.findByRoleName(RoleName.ROLE_USER));
		} else {
			// Xác định quyền dựa trên danh sách quyền được truyền lên
			userRegister.getRoles().forEach(role -> {
				switch (role) {
					case "admin":
						roles.add(roleService.findByRoleName(RoleName.ROLE_ADMIN));
					case "user":
						roles.add(roleService.findByRoleName(RoleName.ROLE_USER));
						break;
					default:
						try {
							throw new AppException("Role not found");
						} catch (AppException e) {
							throw new RuntimeException(e);
						}
				}
			});
		}
		userRepository.save(Users.builder()
				  .fullName(userRegister.getFullName())
				  .username(userRegister.getUsername())
				  .password(passwordEncoder.encode(userRegister.getPassword()))
				  .roles(roles)
				  .status(true)
				  .build());
	}
	@Override
	public JwtResponse login(UserLogin userLogin) throws AppException{
		Authentication authentication;
		try {
			authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getUsername(), userLogin.getPassword()));
		} catch (AuthenticationException e) {
			throw new AppException("Username or Password is incorrect");
		}
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		if (!userPrincipal.isStatus()) {
			throw new AppException("Account is locked. Please contact the administrator.");
		}
		return JwtResponse.builder()
				  .token(jwtProvider.generateToken(userPrincipal))
				  .expired(EXPIRED)
				  .fullName(userPrincipal.getFullName())
				  .username(userPrincipal.getUsername())
				  .roles(userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
				  .status(userPrincipal.isStatus())
				  .build();
	}

	@Override
	public boolean toggleUserStatus(Long userId) throws AppException {
		Users user = userRepository.findByUserId(userId)
				.orElseThrow(() -> new AppException("User not found"));
		user.setStatus(!user.isStatus());
		userRepository.save(user);
		return user.isStatus();
	}
	@Override
	public void changePassword(String username, String newPassword) throws AppException{
		Users user = userRepository.findByUsername(username)
				.orElseThrow(() -> new AppException("User not found"));

		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}
	@Override
	public boolean checkPassword(String username, String password) throws AppException{
		Users user = userRepository.findByUsername(username)
				.orElseThrow(() -> new AppException("User not found"));

		return passwordEncoder.matches(password, user.getPassword());
	}

	@Override
	public void updateUser(Users user) {
		userRepository.save(user);
	}

	@Override
	public void addAddress(String username, AddressRequest addressRequest) {
		Users user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));

		Address newAddress = Address.builder()
				.fullAddress(addressRequest.getFullAddress())
				.user(user)
				.build();

		user.getAddress().add(newAddress);
		userRepository.save(user);
	}
	@Override
	public void deleteAddress(String username, Long addressId) throws AppException {
		Users user = userRepository.findByUsername(username)
				.orElseThrow(() -> new AppException("User not found"));
		Optional<Address> addressToRemove = user.getAddress()
				.stream()
				.filter(address -> address.getAddressId().equals(addressId))
				.findFirst();

		if (addressToRemove.isPresent()) {
			Address address = addressToRemove.get();
			if (address.getUser().equals(user)) {
				addressRepository.deleteById(addressId);
			} else {
				throw new AppException("Address does not belong to the user");
			}
		} else {
			throw new AppException("Address not found");
		}
	}

	@Override
	public List<AddressResponse> getUserAddresses(String username) throws AppException {
		Users user = userRepository.findByUsername(username)
				.orElseThrow(() -> new AppException("User not found"));

		return user.getAddress()
				.stream()
				.map(address -> AddressResponse.builder()
						.addressId(address.getAddressId())
						.fullAddress(address.getFullAddress())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public AddressResponse getUserAddressById(String username, Long addressId) throws AppException {
		Users user = userRepository.findByUsername(username)
				.orElseThrow(() -> new AppException("User not found"));

		Optional<Address> address = user.getAddress()
				.stream()
				.filter(a -> a.getAddressId().equals(addressId))
				.findFirst();

		if (address.isPresent()) {
			return AddressResponse.builder()
					.addressId(address.get().getAddressId())
					.fullAddress(address.get().getFullAddress())
					.build();
		} else {
			throw new AppException("Address not found");
		}
	}
}

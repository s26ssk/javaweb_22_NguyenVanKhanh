package com.la.javaweb.repository;

import com.la.javaweb.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByUsername(String username);
	Optional<Users> findByUserId(Long userId);
	
	Boolean existsByUsername(String username);
	Page<Users> findAll(Pageable pageable);
	Page<Users> findByUsernameContaining(String username, Pageable pageable);
}

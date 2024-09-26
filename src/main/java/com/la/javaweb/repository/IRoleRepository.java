package com.la.javaweb.repository;

import com.la.javaweb.model.RoleName;
import com.la.javaweb.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<Roles, Long> {
	
	Optional<Roles> findByRoleName(RoleName roleName);
	
}

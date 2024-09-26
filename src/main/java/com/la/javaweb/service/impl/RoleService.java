package com.la.javaweb.service.impl;

import com.la.javaweb.model.RoleName;
import com.la.javaweb.model.Roles;
import com.la.javaweb.repository.IRoleRepository;
import com.la.javaweb.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService implements IRoleService {
	
	@Autowired
	private IRoleRepository roleRepository;
	
	@Override
	public Roles findByRoleName(RoleName roleName) {
		return roleRepository.findByRoleName(roleName).orElseThrow(() -> new RuntimeException("role not found"));
	}

	@Override
	public List<Roles> getAllRoles() {
		return roleRepository.findAll();
	}
}

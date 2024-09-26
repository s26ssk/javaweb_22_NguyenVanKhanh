package com.la.javaweb.service;

import com.la.javaweb.model.RoleName;
import com.la.javaweb.model.Roles;

import java.util.List;

public interface IRoleService {
	
	Roles findByRoleName(RoleName roleName);
	List<Roles> getAllRoles();

}

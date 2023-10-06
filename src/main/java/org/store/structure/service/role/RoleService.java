package org.store.structure.service.role;

import org.store.structure.model.Role;

import java.util.List;

public interface RoleService {
    Role save(Role role);
    void delete(Long id);
    Role findByRoleName(String roleName);
    List<Role> findAll();
}

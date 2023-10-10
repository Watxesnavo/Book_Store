package org.store.structure.service.role;

import java.util.List;
import org.store.structure.model.Role;

public interface RoleService {
    Role save(Role role);

    void delete(Long id);

    Role findByRoleName(String roleName);

    List<Role> findAll();
}

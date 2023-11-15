package org.store.structure.service.role;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.store.structure.model.Role;
import org.store.structure.repository.role.RoleRepository;

@Component
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public Role findByRoleName(String roleName) {
        return roleRepository.findByRoleName(Role.RoleName.valueOf(roleName));
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}

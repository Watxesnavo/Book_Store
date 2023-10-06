package org.store.structure.service.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.store.structure.model.Role;
import org.store.structure.repository.role.RoleRepository;

import java.util.List;

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
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .filter(role -> role.getRoleName().name().equals(roleName))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}

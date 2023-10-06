package org.store.structure.repository.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.store.structure.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}

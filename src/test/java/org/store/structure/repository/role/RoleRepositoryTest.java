package org.store.structure.repository.role;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.store.structure.model.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Sql(scripts = {
            "classpath:database/roles/clean-roles-table-before.sql",
            "classpath:database/roles/add-roles-into-roles-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/roles/delete-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void findByRoleName_WithValidRoleName_ReturnsRole() {
        Role userRole = roleRepository.findByRoleName(Role.RoleName.USER);
        Role adminRole = roleRepository.findByRoleName(Role.RoleName.ADMIN);

        assertEquals(1, userRole.getId());
        assertEquals(2, adminRole.getId());
        assertEquals("USER", userRole.getRoleName().name());
        assertEquals("ADMIN", adminRole.getRoleName().name());
    }
}

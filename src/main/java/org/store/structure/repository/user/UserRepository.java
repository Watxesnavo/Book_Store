package org.store.structure.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.store.structure.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

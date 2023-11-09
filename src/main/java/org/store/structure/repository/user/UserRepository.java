package org.store.structure.repository.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.store.structure.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}

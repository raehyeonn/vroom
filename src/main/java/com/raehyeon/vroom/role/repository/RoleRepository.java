package com.raehyeon.vroom.role.repository;

import com.raehyeon.vroom.role.domain.Role;
import com.raehyeon.vroom.role.domain.RoleType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleType(RoleType roleType);
    boolean existsByRoleType(RoleType roleType);

}

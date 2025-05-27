package com.raehyeon.vroom.role;

import com.raehyeon.vroom.role.domain.Role;
import com.raehyeon.vroom.role.domain.RoleType;
import com.raehyeon.vroom.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!roleRepository.existsByRoleType(RoleType.MEMBER)) {
            Role memberRole = Role.builder()
                .roleType(RoleType.MEMBER)
                .build();
            roleRepository.save(memberRole);
        }

        if (!roleRepository.existsByRoleType(RoleType.ADMIN)) {
            Role memberRole = Role.builder()
                .roleType(RoleType.ADMIN)
                .build();
            roleRepository.save(memberRole);
        }
    }

}

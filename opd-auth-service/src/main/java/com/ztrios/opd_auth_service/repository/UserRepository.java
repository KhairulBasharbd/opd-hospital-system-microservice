package com.ztrios.opd_auth_service.repository;

import com.ztrios.opd_auth_service.entity.UserEntity;
import com.ztrios.opd_auth_service.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository <UserEntity, UUID> {
        Boolean existsByEmail(String email);
        Optional<UserEntity> findByEmail(String email);
        Boolean existsByRole(Role role);
}

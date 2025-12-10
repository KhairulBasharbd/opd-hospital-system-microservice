package com.ztrios.opd_auth_service.repository;

import com.ztrios.opd_auth_service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository <UserEntity, UUID> {


}

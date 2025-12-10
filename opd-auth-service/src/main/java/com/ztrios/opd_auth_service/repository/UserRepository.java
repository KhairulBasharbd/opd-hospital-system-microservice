package com.ztrios.opd_auth_service.repository;

import com.ztrios.opd_auth_service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository <UserEntity, UUID> {


}

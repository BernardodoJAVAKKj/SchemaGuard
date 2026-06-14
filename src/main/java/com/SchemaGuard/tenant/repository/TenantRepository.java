package com.SchemaGuard.tenant.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SchemaGuard.tenant.model.Tenant;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findBySchemaName(String schemaName);
    boolean existsBySchemaName(String schemaName);
}
package com.SchemaGuard.tenant.service;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SchemaGuard.tenant.model.Tenant;
import com.SchemaGuard.tenant.repository.TenantRepository;

@Service
public class TenantProvisioningService {

    @Autowired
    private DataSource dataSource;
    

    @Autowired
    private TenantRepository tenantRepository;

    public void createTenantSchema(String tenantName) throws Exception {
        // limpa o nome pra virar um schema válido no postgres
        String schemaName = tenantName.toLowerCase().replaceAll("[^a-z0-9]", "_");

        if (tenantRepository.existsBySchemaName(schemaName)) {
            throw new RuntimeException("Tenant já existe: " + schemaName);
        }

        // 1. cria o schema no banco
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
        }

      

        // 3. persiste o tenant na tabela global
        Tenant tenant = new Tenant();
        tenant.setName(tenantName);
        tenant.setSchemaName(schemaName);
        tenantRepository.save(tenant);
    }

    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    public Tenant findById(UUID id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant não encontrado"));
    }
}
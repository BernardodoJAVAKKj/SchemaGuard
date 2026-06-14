package com.SchemaGuard.tenant.controller;

import com.SchemaGuard.tenant.dto.TenantRequest;
import com.SchemaGuard.tenant.service.TenantProvisioningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tenants")
public class TenantController {

    @Autowired
    private TenantProvisioningService tenantProvisioningService;

    @PostMapping
    public ResponseEntity<String> createTenant(@RequestBody TenantRequest request) throws Exception {
        tenantProvisioningService.createTenantSchema(request.getName());
        return ResponseEntity.ok("Tenant criado: " + request.getName());
    }
}

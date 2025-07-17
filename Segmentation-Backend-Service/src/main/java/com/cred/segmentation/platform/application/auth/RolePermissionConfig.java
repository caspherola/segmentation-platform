package com.cred.segmentation.platform.application.auth;

import com.cred.segmentation.platform.application.model.Permission;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RolePermissionConfig {
    private final Map<String, Set<Permission>> rolePermissions;

    public RolePermissionConfig() {
        rolePermissions = new HashMap<>();

        // Admin - all permissions
        rolePermissions.put("admin", EnumSet.allOf(Permission.class));

        // Manager - all except delete
        rolePermissions.put("manager", EnumSet.of(
                Permission.VIEW, Permission.CREATE, Permission.UPDATE
        ));

        // Analyst - only view and create
        rolePermissions.put("analyst", EnumSet.of(
                Permission.VIEW, Permission.CREATE
        ));

        // Marketer - only view and create
        rolePermissions.put("marketer", EnumSet.of(
                Permission.VIEW, Permission.CREATE
        ));

        // operations - only view
        rolePermissions.put("operations", EnumSet.of(
                Permission.VIEW
        ));
    }

    public Set<Permission> getPermissions(String role) {
        return rolePermissions.getOrDefault(role.toLowerCase(), Collections.emptySet());
    }

    public boolean hasPermission(String role, Permission permission) {
        return getPermissions(role).contains(permission);
    }

}

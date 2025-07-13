package com.cred.segmentation.platform.application.service.impl;

import com.cred.segmentation.platform.application.auth.RolePermissionConfig;
import com.cred.segmentation.platform.application.model.Permission;
import com.cred.segmentation.platform.application.model.SegmentationCreateUser;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final RuleCreationUserService userService;
    private final RolePermissionConfig rolePermissionConfig;

    public AuthenticationService(RuleCreationUserService userService, RolePermissionConfig rolePermissionConfig) {
        this.userService = userService;
        this.rolePermissionConfig = rolePermissionConfig;
    }

    public SegmentationCreateUser authenticate(String token) {
        return userService.findByToken(token).orElse(null);
    }

    public boolean authorize(SegmentationCreateUser user, Permission permission) {
        if (user == null) return false;
        return rolePermissionConfig.hasPermission(user.getRole(), permission);
    }
}

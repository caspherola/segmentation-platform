package com.cred.segmentation.platform.application.service.impl;

import com.cred.segmentation.platform.application.model.SegmentationCreateUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RuleCreationUserService {
    private final Map<String, SegmentationCreateUser> users;

    public RuleCreationUserService() {
        users = new HashMap<>();

        // Predefined users for demonstration
        // Token format: {role}-token (simple for demo, use JWT in production)

        // Admin user - has all permissions
        users.put("admin-token", new SegmentationCreateUser("admin", "admin"));

        // Manager user - has all permissions except delete
        users.put("manager-token", new SegmentationCreateUser("manager", "manager"));

        // Analyst user - has view and create permissions only
        users.put("analyst-token", new SegmentationCreateUser("analyst", "analyst"));

        // Marketer user - has view and create permissions only
        users.put("marketer-token", new SegmentationCreateUser("marketer", "marketer"));
    }

    public Optional<SegmentationCreateUser> findByToken(String token) {
        return Optional.ofNullable(users.get(token));
    }

    public void addUser(String token, SegmentationCreateUser user) {
        users.put(token, user);
    }

    // Method to remove users (for token invalidation)
    public void removeUser(String token) {
        users.remove(token);
    }

    // Method to check if token exists
    public boolean tokenExists(String token) {
        return users.containsKey(token);
    }

    // Method to get all users (for admin purposes)
    public Map<String, SegmentationCreateUser> getAllUsers() {
        return new HashMap<>(users); // Return copy to prevent external modification
    }

    // Method to validate user credentials (if you had password-based auth)
    public Optional<SegmentationCreateUser> validateCredentials(String username, String password) {
        // In a real implementation, this would:
        // 1. Hash the provided password
        // 2. Compare with stored hash
        // 3. Return user if valid, empty if invalid
        return users.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }
}

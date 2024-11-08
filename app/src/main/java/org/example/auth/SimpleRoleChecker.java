package org.example.auth;

import java.util.List;

public class SimpleRoleChecker implements RoleChecker {

    @Override
    public boolean hasRequiredRoles(List<String> roles, String... requiredRoles) {
        for (String role : requiredRoles) {
            if (roles.contains(role)) {
                return true;
            }
        }
        return false;
    }
}
